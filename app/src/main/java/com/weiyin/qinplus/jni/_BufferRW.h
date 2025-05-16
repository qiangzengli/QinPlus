/*
 * _BufferRW.h
 *
 *  Created on: 2016年9月21日
 *      Author: LyReonn
 */

#ifndef BUFFERRW_H_
#define BUFFERRW_H_

void SkipByte(BYTE **buffer, DWORD count);

BYTE ReadBuf(BYTE **buffer);
// Returned array must be freed MANUALLY!!!
BYTE *ReadArr(BYTE **buffer, DWORD count);
// Returned array must be freed MANUALLY!!!
char *ReadStr(BYTE **buffer, DWORD count);
DWORD ReadVal(BYTE **buffer, DWORD count);
DWORD ReadVarLenVal(BYTE **buffer);

void WriteBuf(BYTE **buffer, BYTE value);
void WriteArr(BYTE **buffer, BYTE *array, DWORD count);
void WriteStr(BYTE **buffer, const char *string);
void WriteVal(BYTE **buffer, DWORD value, DWORD count);
void WriteVarLenVal(BYTE **buffer, DWORD value);

#endif /* BUFFERRW_H_ */
