/*
 * _MidiFileRW.c
 *
 *  Created on: 2016��9��21��
 *      Author: LyReonn
 */

#include <stdio.h>
#include <stdlib.h>
#include "_MidiFileRW.h"
#include "_BufferRW.h"

#define MThd 0x4D546864
#define MTrk 0x4D54726B
#define TrkN 0x00FF0300
#define EoT  0x00FF2F00

BYTE VerifyHeader(BYTE *);
BYTE VerifyStruct(MIDIFILE *);

void ReadHdrChunk(MIDIFILE *, BYTE **);
void ReadTrkChunk(MIDIFILE *, BYTE *);
void GetEvtCount(MIDIFILE *, BYTE **);
void ReadEvent(EVENT *, BYTE **);

void SortEvent(EVENT *, DWORD);
void FilterEvent(MIDIFILE *);
void FormatEvent(MIDIFILE *, BYTE);
int CalcFileSize(MIDIFILE *, DWORD *);

BYTE GetLen(DWORD);
void ResetStaticVar(void);
void WriteFileBuf(BYTE **, MIDIFILE *, DWORD *);
void WriteEvent(BYTE **, EVENT *);

MIDIFILE *ReadMidiFile(const char *fileName) {
	FILE *stream = NULL;
	BYTE *buffer = NULL;
	int fileSize;
	MIDIFILE *midiFile = NULL;

	stream = fopen(fileName, "rb");
	if (NULL == stream)
		goto ERR;

	fseek(stream, 0, SEEK_END);
	fileSize = ftell(stream);
	rewind(stream);
	if (-1 == fileSize)
		goto ERR;

	buffer = (BYTE *)calloc(fileSize, sizeof(BYTE));
	if (0 == fread(buffer, sizeof(BYTE), fileSize, stream))
		goto ERR;

	if (0 == VerifyHeader(buffer))
		goto ERR;

	midiFile = (MIDIFILE *)malloc(sizeof(MIDIFILE));
	ReadHdrChunk(midiFile, &buffer);
	if (midiFile->Format > 1) {
ERR:	free(midiFile);
		free(buffer);
		fclose(stream);
		return NULL;
	}

	midiFile->EvtCount = 0;
	midiFile->FirstEvt = NULL;
	ReadTrkChunk(midiFile, buffer);

	midiFile->FirstEvt = (EVENT *)calloc(midiFile->EvtCount, sizeof(EVENT));
	ReadTrkChunk(midiFile, buffer);

	free(buffer);
	fclose(stream);

	return midiFile;
}

void ReadHdrChunk(MIDIFILE *midiFile, BYTE **buffer) {
	DWORD hdrLen;

	SkipByte(buffer, 4);
	hdrLen = ReadVal(buffer, 4);
	midiFile->Format = ReadVal(buffer, 2);
	midiFile->TrkCount = ReadVal(buffer, 2);
	midiFile->Division = ReadVal(buffer, 2);

//	HONOR the header chunk's length!!
	SkipByte(buffer, hdrLen - 6);
}

void ReadTrkChunk(MIDIFILE *midiFile, BYTE *curBuf) {
	EVENT *curEvt = midiFile->FirstEvt;
	DWORD absTicks = 0;
	WORD curTrk = 1;
	BYTE *trkBeg;
	DWORD trkLen;

	do {
//		Skip non-track chunks
		while (MTrk != ReadVal(&curBuf, 4))
			SkipByte(&curBuf, ReadVal(&curBuf, 4));

		trkLen = ReadVal(&curBuf, 4);
		trkBeg = curBuf;
		while (curBuf - trkBeg < trkLen) {
			absTicks += ReadVarLenVal(&curBuf);
			if (NULL == curEvt)
				GetEvtCount(midiFile, &curBuf);
			else {
				curEvt->TrkNum = curTrk;
				curEvt->AbsTicks = absTicks;
				ReadEvent(curEvt, &curBuf);
				curEvt++;
			}
		}

		absTicks = 0;
		curTrk++;
	} while (curTrk <= midiFile->TrkCount);
}

void GetEvtCount(MIDIFILE *midiFile, BYTE **buffer) {
	static BYTE _runningStat;
	BYTE curByte;

	curByte = ReadBuf(buffer);
	switch (curByte) {
	case 0xFF:
		(*buffer)++;
		/* no break */
	case 0xF7:
	case 0xF0:
		midiFile->EvtCount++;
		SkipByte(buffer, ReadVarLenVal(buffer));
		return;

	default:
		midiFile->EvtCount++;
		if (curByte & 0x80) {
			_runningStat = curByte;
			(*buffer)++;
		}
		if (_runningStat < 0xC0 || _runningStat > 0xDF)
			(*buffer)++;
	}
}

void ReadEvent(EVENT *event, BYTE **buffer) {
	static BYTE _runningStat;
	BYTE curByte;

	curByte = ReadBuf(buffer);
	switch (curByte) {
	case 0xFF:
	case 0xF7:
	case 0xF0:
		event->Type = (EVENTTYPE)curByte;
		event->MetaType = (META == event->Type)? (METATYPE)ReadBuf(buffer): 0;
		event->Len = ReadVarLenVal(buffer);
		event->Data = ReadArr(buffer, event->Len);
		return;

	default:
		if (curByte & 0x80) {
			_runningStat = curByte;
			curByte = ReadBuf(buffer);
		}
		event->Type = (EVENTTYPE)(_runningStat >> 4);
		event->Chn = _runningStat & 0xF;
		event->Len = (PROGCHG == event->Type || CHNAFT == event->Type)? 2: 3;
		event->Data = (BYTE *)calloc(event->Len, sizeof(BYTE));
		event->Data[0] = _runningStat;
		event->Data[1] = curByte;
		if (3 == event->Len)
			event->Data[2] = ReadBuf(buffer);
		if (NOTEON == event->Type && 0 == event->Data[2])
			event->Type = NOTEOFF;
	}
}

BYTE WriteMidiFile(const char *fileName, MIDIFILE *midiFile, BYTE format) {
	DWORD *trkLen = NULL;
	FILE *stream = NULL;
	BYTE *buffer = NULL;
	int fileSize;

	if (0 == VerifyStruct(midiFile) || format > 1)
		return 0;

	FilterEvent(midiFile);
	if (0 == midiFile->EvtCount)
		goto ERR;

	SortEvent(midiFile->FirstEvt, midiFile->EvtCount);
	FormatEvent(midiFile, format);

	trkLen = (DWORD *)calloc(midiFile->TrkCount + 1, sizeof(DWORD));
	fileSize = CalcFileSize(midiFile, trkLen);

	buffer = (BYTE *)calloc(fileSize, sizeof(BYTE));
	WriteFileBuf(&buffer, midiFile, trkLen);

	buffer -= fileSize;
	if (0 == VerifyHeader(buffer))
		goto ERR;

	stream = fopen(fileName, "wb");
	if (NULL == stream)
		goto ERR;

	if (0 == fwrite(buffer, sizeof(BYTE), fileSize, stream)) {
ERR:	free(trkLen);
		free(buffer);
		fclose(stream);
		return 0;
	}

	free(trkLen);
	free(buffer);
	fclose(stream);

	return 1;
}

void FilterEvent(MIDIFILE *midiFile) {
	EVENT *curEvt = midiFile->FirstEvt;
	EVENT *event = NULL;
	DWORD count = 0;
	DWORD i;
	for (i = 0; i < midiFile->EvtCount; i++)
		if (EventFilter(curEvt[i]))
			count++;

	midiFile->EvtCount = count;
	if (0 == count)
		return;

	event = (EVENT *)calloc(count, sizeof(EVENT));
	DWORD j;
	for (j = 0; j < count; j++) {
		while (DISCARD == EventFilter(*curEvt)) {
			free(curEvt->Data);
			curEvt++;
		}
		event[j] = *curEvt++;
	}

//	free(midiFile->FirstEvt);
	midiFile->FirstEvt = event;
}

FILTER EventFilter(EVENT event) {
	switch (event.Type) {
	case SYSEXF0:
	case SYSEXF7:
		return DISCARD;

	case META:
		switch (event.MetaType) {
		case SETTEMPO:
		case TIMESIG:
			return KEEP_TRK1;

//		case TRKNAME:
//			return KEEP_TRK_TIME0;

		case ENDOFTRK:
			break;
		default:
			break;
		}
		return DISCARD;

	default:
		return KEEP;
	}
}

void SortEvent(EVENT *event, DWORD count) {
	int tmp;
	EVENT tmpEvt;
	DWORD i;
	for (i = 0; i < count - 1; i++)
		if (event[i].AbsTicks > event[i+1].AbsTicks) {
			tmp = i;
			do {
				tmpEvt = event[tmp];
				event[tmp] = event[tmp+1];
				event[tmp+1] = tmpEvt;
			} while (--tmp >= 0 && event[tmp].AbsTicks > event[tmp+1].AbsTicks);
		}
}

void FormatEvent(MIDIFILE *midiFile, BYTE fmt) {
	EVENT *curEvt = midiFile->FirstEvt;
	DWORD count = midiFile->EvtCount;
	WORD chnFlag = 0;
	BYTE trkNum = 1;
	EVENT *event = NULL;

	if (0 == fmt) {
		DWORD i;
		for ( i = 0; i < count; i++)
			curEvt[i].TrkNum = 1;

		midiFile->Format = 0;
		midiFile->TrkCount = 1;

		return;
	}
	DWORD i;
	for (i = 0; i < count; i++)
		switch (EventFilter(curEvt[i])) {
		case KEEP_TRK1:
			curEvt[i].TrkNum = 1;
			break;
		case KEEP:
			chnFlag |= 1 << curEvt[i].Chn;
			break;
		default:;
		}
	BYTE chn;
	for (chn = 0; chn <= 0xF; chn++)
		if ((1 << chn) & chnFlag) {
			trkNum++;
			DWORD i;
			for (i = 0; i < count; i++)
				if (curEvt[i].Type < 0xF && curEvt[i].Chn == chn)
					curEvt[i].TrkNum = trkNum;
		}

	event = (EVENT *)calloc(count, sizeof(EVENT));
	BYTE trk;
	for (trk = 1; trk <= trkNum; trk++)
	{
		DWORD i;
		for (i = 0; i < count; i++)
			if (curEvt[i].TrkNum == trk)
				*event++ = curEvt[i];
	}
	event -= count;
	free(midiFile->FirstEvt);
	midiFile->FirstEvt = event;

	midiFile->Format = 1;
	midiFile->TrkCount = trkNum;
}

int CalcFileSize(MIDIFILE *midiFile, DWORD *trkLen) {
	EVENT *curEvt = midiFile->FirstEvt;
	BYTE runningStat = 0;
	DWORD lastTick = 0;
	int dltTicks;
	int fileSize = 0;

//	Header chunk's length:
	trkLen[0] = 6;
//	MThd + hdrLen:
	fileSize += 8;
	fileSize += trkLen[0];
	BYTE trk;
	for (trk = 1; trk <= midiFile->TrkCount; trk++) {
//		MTrk + trkLen:
		fileSize += 8;

		trkLen[trk] = 0;
//		Trk Name:
		trkLen[trk] += 4;

		while (curEvt->TrkNum == trk) {
			dltTicks = curEvt->AbsTicks - lastTick;
			dltTicks = (dltTicks > 0)? dltTicks: 0;
			lastTick = curEvt->AbsTicks;
			trkLen[trk] += GetLen(dltTicks);

			switch (curEvt->Type) {
			case META:
				trkLen[trk]++;
				/* no break */
			case SYSEXF7:
			case SYSEXF0:
				trkLen[trk]++;
				trkLen[trk] += GetLen(curEvt->Len);
				trkLen[trk] += curEvt->Len;
				runningStat = curEvt->Type;
				break;

			default:
				trkLen[trk] += curEvt->Len - 1;
				if (curEvt->Data[0] != runningStat) {
					trkLen[trk]++;
					runningStat = curEvt->Data[0];
				}
			}

			curEvt++;
		}

//		End of Trk:
		trkLen[trk] += 4;
		fileSize += trkLen[trk];

		lastTick = 0;
		runningStat = 0;
	}

	return fileSize;
}

BYTE GetLen(DWORD varLenVal) {
	BYTE count = 1;

	while (varLenVal >>= 7)
		count++;

	return count;
}

void WriteFileBuf(BYTE **buffer, MIDIFILE *midiFile, DWORD *trkLen) {
	EVENT *curEvt = midiFile->FirstEvt;

	WriteStr(buffer, "MThd");
	WriteVal(buffer, trkLen[0], 4);
	WriteVal(buffer, midiFile->Format, 2);
	WriteVal(buffer, midiFile->TrkCount, 2);
	WriteVal(buffer, midiFile->Division, 2);

	BYTE trk;
	for (trk = 1; trk <= midiFile->TrkCount; trk++) {
		WriteStr(buffer, "MTrk");
		WriteVal(buffer, trkLen[trk], 4);
		WriteVal(buffer, TrkN, 4);

		ResetStaticVar();
		while (curEvt->TrkNum == trk) {
			WriteEvent(buffer, curEvt);
			curEvt++;
		}

		WriteVal(buffer, EoT, 4);
	}
}

void WriteEvent(BYTE **buffer, EVENT *event) {
	static BYTE _runningStat = 0;
	static DWORD _lastTick = 0;
	int dltTicks;

	if (NULL == event) {
		_runningStat = 0;
		_lastTick = 0;
		return;
	}

	dltTicks = event->AbsTicks - _lastTick;
	dltTicks = (dltTicks > 0)? dltTicks: 0;
	_lastTick = event->AbsTicks;
	WriteVarLenVal(buffer, dltTicks);

	switch (event->Type) {
	case META:
	case SYSEXF7:
	case SYSEXF0:
		WriteBuf(buffer, event->Type);
		if (META == event->Type)
			WriteBuf(buffer, event->MetaType);
		WriteVarLenVal(buffer, event->Len);
		WriteArr(buffer, event->Data, event->Len);
		_runningStat = event->Type;
		return;

	default:
		if (event->Data[0] != _runningStat) {
			WriteBuf(buffer, event->Data[0]);
			_runningStat = event->Data[0];
		}
		DWORD i;
		for (i = 1; i < event->Len; i++)
			WriteBuf(buffer, event->Data[i]);
	}
}

void ResetStaticVar(void) {
	BYTE *tmpBuf = NULL;
	EVENT *tmpEvt = NULL;

	WriteEvent(&tmpBuf, tmpEvt);
}

BYTE VerifyHeader(BYTE *curBuf) {
	if (MThd != ReadVal(&curBuf, 4))
		return 0;

	return 1;
}

BYTE VerifyStruct(MIDIFILE *midiFile) {
	if (midiFile->Format > 1
		|| 0 == midiFile->Division
		|| 0 == midiFile->TrkCount
		|| 0 == midiFile->EvtCount
		|| NULL == midiFile->FirstEvt)
		return 0;

	return 1;
}

void FreeMidiFile(MIDIFILE *midiFile) {
	if (NULL == midiFile)
		return;

	if (NULL == midiFile->FirstEvt) {
		free(midiFile);
		return;
	}
	DWORD i;
	for (i = 0; i < midiFile->EvtCount; i++) {
		free(midiFile->FirstEvt[i].Data);
		midiFile->FirstEvt[i].Data = NULL;
	}

	free(midiFile->FirstEvt);
	midiFile->FirstEvt = NULL;

	free(midiFile);
}

void FreeFileBuf(BYTE *buffer) {
	free(buffer);
}

BYTE GenerateFileBuf(BYTE **buffer, int *bufSize, MIDIFILE *midiFile) {
	DWORD *trkLen;

	FilterEvent(midiFile);
	SortEvent(midiFile->FirstEvt, midiFile->EvtCount);
	FormatEvent(midiFile, 0);

	trkLen = (DWORD *)calloc(midiFile->TrkCount + 1, sizeof(DWORD));
	*bufSize = CalcFileSize(midiFile, trkLen);

	*buffer = (BYTE *)calloc(*bufSize, sizeof(BYTE));
	WriteFileBuf(buffer, midiFile, trkLen);

	*buffer -= *bufSize;
	if (0 == VerifyHeader(*buffer)) {
		free(trkLen);
		free(*buffer);
		*buffer = NULL;
		*bufSize = 0;
		return 0;
	}

	free(trkLen);
	return 1;
}

BYTE WriteRecordedEvents(BYTE **buffer, int *bufSize, EVENT *firstEvt, DWORD evtCount) {
	MIDIFILE *midiFile = NULL;
	EVENT *event = NULL;

	if (NULL == firstEvt || 0 == evtCount)
		return 0;

	midiFile = (MIDIFILE *)malloc(sizeof(MIDIFILE));
	event = (EVENT *)calloc(evtCount, sizeof(EVENT));
	DWORD i;
	for ( i = 0; i < evtCount; i++) {
		event[i].TrkNum = 1;
		event[i].AbsTicks = (float)firstEvt[i].TimeStamp * 480 / 5E5 ;
		event[i].Type = (EVENTTYPE)(firstEvt[i].Data[0] >> 4);
		event[i].Chn = firstEvt[i].Data[0] & 0xF;
		event[i].Len = firstEvt[i].Len;
		event[i].Data = firstEvt[i].Data;
	}

	midiFile->Format = 0;
	midiFile->TrkCount = 1;
	midiFile->Division = 480;
	midiFile->EvtCount = evtCount;
	midiFile->FirstEvt = event;

	if (0 == GenerateFileBuf(buffer, bufSize, midiFile)) {
		free(midiFile);
		free(event);
		return 0;
	}

	free(midiFile);
	free(event);
	return 1;
}

BYTE WriteRecordedEventsIntoFile(const char *fileName, EVENT *firstEvt, DWORD evtCount) {
	MIDIFILE *midiFile = NULL;
	EVENT *event = NULL;

	if (NULL == firstEvt || 0 == evtCount)
		return 0;

	midiFile = (MIDIFILE *)malloc(sizeof(MIDIFILE));
	event = (EVENT *)calloc(evtCount, sizeof(EVENT));
	DWORD i;
	for (i = 0; i < evtCount; i++) {
		event[i].TrkNum = 1;
		event[i].AbsTicks = (float)firstEvt[i].TimeStamp / 5E5 * 480;
		event[i].Type = (EVENTTYPE)(firstEvt[i].Data[0] >> 4);
		event[i].Chn = firstEvt[i].Data[0] & 0xF;
		event[i].Len = firstEvt[i].Len;
		event[i].Data = firstEvt[i].Data;
	}

	midiFile->Format = 0;
	midiFile->TrkCount = 1;
	midiFile->Division = 480;
	midiFile->EvtCount = evtCount;
	midiFile->FirstEvt = event;

	if (0 == WriteMidiFile(fileName, midiFile, 0)) {
		free(midiFile);
		free(event);
		return 0;
	}

	free(midiFile);
	free(event);
	return 1;
}
