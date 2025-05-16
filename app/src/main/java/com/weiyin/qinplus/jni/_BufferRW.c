/*
 * _BufferRW.c
 *
 *  Created on: 2016年9月21日
 *      Author: LyReonn
 */

#include <stdlib.h>
#include "_MidiFileRW.h"

void SkipByte(BYTE **buffer, DWORD count) {
	*buffer += count;
}

BYTE ReadBuf(BYTE **buffer) {
	return (*(*buffer)++);
}

BYTE *ReadArr(BYTE **buffer, DWORD count) {
	BYTE *array = NULL;

	if (0 == count)
		return NULL;

	array = (BYTE *)calloc(count, sizeof(BYTE));
	DWORD i;
	for (i = 0; i < count; i++)
		array[i] = ReadBuf(buffer);

	return array;
}

char *ReadStr(BYTE **buffer, DWORD count) {
	char *string = NULL;

	if (0 == count)
		return NULL;

	string = (char *)calloc(count + 1, sizeof(char));
	DWORD i;
	for ( i = 0; i < count; i++)
		string[i] = ReadBuf(buffer);
	string[count] = '\0';

	return string;
}

DWORD ReadVal(BYTE **buffer, DWORD count) {
	DWORD value = 0;
	DWORD i;
	for ( i= 0; i < count; i++) {
		value <<= 8;
		value += ReadBuf(buffer);
	}

	return value;
}

DWORD ReadVarLenVal(BYTE **buffer) {
	DWORD value = 0;

	while (**buffer & 0x80) {
		value += ReadBuf(buffer) & 0x7F;
		value <<= 7;
	}
	value += ReadBuf(buffer);

	return value;
}

void WriteBuf(BYTE **buffer, BYTE value) {
	*(*buffer)++ = value;
}

void WriteArr(BYTE **buffer, BYTE *array, DWORD count) {
	if (NULL == array)
		return;
	DWORD i;
	for (i= 0; i < count; i++)
		WriteBuf(buffer, array[i]);
}

void WriteStr(BYTE **buffer, const char *string) {
	if (NULL == string)
		return;
	DWORD i;
	for ( i= 0; string[i] != '\0'; i++)
		WriteBuf(buffer, string[i]);
}

void WriteVal(BYTE **buffer, DWORD value, DWORD count) {
	DWORD i;
	for (i= 1; i <= count; i++)
		WriteBuf(buffer, (value >> (8 * (count - i))) & 0xFF);
}

void WriteVarLenVal(BYTE **buffer, DWORD value) {				// Value > 0x0FFFFFFF ？？
	BYTE tmpByte[4] = {0, 0, 0, 0};
	BYTE i = 0;

	do tmpByte[i++] = value & 0x7F;
	while (value >>= 7);

	for (i--; i > 0; i--)
		WriteBuf(buffer, tmpByte[i] | 0x80);
	WriteBuf(buffer, tmpByte[0]);
}
