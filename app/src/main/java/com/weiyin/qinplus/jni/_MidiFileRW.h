/*
 * MidiFileRW.h
 *
 *  Created on: 2016年9月21日
 *      Author: LyReonn
 */

#ifndef MIDIFILERW_H_
#define MIDIFILERW_H_

typedef unsigned char  BYTE;
typedef unsigned short WORD;
typedef unsigned int   DWORD;

typedef enum tagEventType {
//	MIDI Event:
	NOTEOFF		= 8,
	NOTEON		= 9,
	KEYAFT		= 0xA,		// Polyphonic Key Pressure, aka Aftertouch
	CC			= 0xB,		// Controller Change
	PROGCHG 	= 0xC,		// Program Change
	CHNAFT		= 0xD,		// Channel Key Pressure
	PITCHBD		= 0xE,		// Pitch Bend
//	Sysex Event:
	SYSEXF0		= 0xF0,		// System Exclusive
	SYSEXF7		= 0xF7,
//	Meta Event:
	META		= 0xFF
} EVENTTYPE;

typedef enum tagMetaType {
	SEQNUM		= 0,		// Sequence Number
	TEXT		= 1,		// Text Event
	COPYRIGHT	= 2,		// Copyright Notice
	TRKNAME		= 3,		// Sequence/Track Name
	INSTNAME	= 4,		// Instrument Name
	LYRIC		= 5,
	MARKER		= 6,
	CUEPOINT	= 7,
	CHNPREFIX	= 0x20,		// MIDI Channel Prefix
	ENDOFTRK	= 0x2F,		// End of Track
	SETTEMPO	= 0x51,		// Set Tempo
	SMTPEOFFSET	= 0x54,		// SMTPE Offset
	TIMESIG		= 0x58,		// Time Signature
	KEYSIG		= 0x59,		// Key Signature
	SEQSPEC		= 0x7F		// Sequencer-Specific Meta-event
} METATYPE;

// For recording events, record (TimeStamp) in microseconds;
// Assign byte count of data to (Len); Assign data bytes as array to (*Data);
// Leaving other arguments unset is OK.
typedef struct tagEvent {
	WORD TrkNum;
	union {
		DWORD AbsTicks;
		DWORD TimeStamp;
	};
	EVENTTYPE Type;
	union {
		BYTE Chn;
		METATYPE MetaType;
	};
	DWORD Len;
	BYTE *Data;
} EVENT;

typedef struct tagMidiFile {
	WORD Format;
	WORD Division;
	WORD TrkCount;
	DWORD EvtCount;
	EVENT *FirstEvt;
} MIDIFILE;

// If any failure occurs, a NULL pointer will be returned;
// MIDI files of format 2 are not supported yet.
MIDIFILE *ReadMidiFile(const char *fileName);

// If any failure occurs, a value of 0 will be returned;
// (format) can only be 0 or 1, format 2 is not supported yet.
BYTE WriteMidiFile(const char *fileName, MIDIFILE *midiFile, BYTE format);

// Used to free midiFile returned by ReadMidiFile;
// It will free (midiFile), (midiFile->FirstEvt) and every (midiFile->FirstEvt->Data).
void FreeMidiFile(MIDIFILE *midiFile);

// A MIDI file buffer of format 0 will be created in (buffer), file size will be in (bufSize);
// (firstEvt), every (firstEvt->Data) and (*buffer) must be freed MANUALLY!!!
BYTE WriteRecordedEvents(BYTE **buffer, int *bufSize, EVENT *firstEvt, DWORD evtCount);

// Used to free file buffer created by WriteRecordedEvents.
void FreeFileBuf(BYTE *buffer);

// DISCARD:
//	TEXT
//	LYRIC
//	CUEPOINT
//	SEQSPEC
// TRK1:
//	SETTEMPO
//	TIMESIG
//	KEYSIG
// TRK1_TIME0:
//	COPYRIGHT
// TRK1_TIME0_ONCE:
//	SEQNUM
//	SMTPEOFFSET
// TRK2:
//	MARKER
// TRK_TIME0:
//	TRK/SEQNAME
//	INSTNAME
typedef enum tagEventFilter {
	DISCARD,
	KEEP,
	KEEP_TRK1,
//	TRK1_TIME0,
//	TRK1_TIME0_ONCE,
//	TRK2,
	KEEP_TRK_TIME0
} FILTER;

FILTER EventFilter(EVENT event);

#endif /* MIDIFILERW_H_ */
