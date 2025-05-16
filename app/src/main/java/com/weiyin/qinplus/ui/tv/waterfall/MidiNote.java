/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */



package com.weiyin.qinplus.ui.tv.waterfall;

import java.security.PublicKey;
import java.util.*;




/** @class MidiNote
 * A MidiNote contains
 *
 * starttime - The time (measured in pulses) when the note is pressed.
 * channel   - The channel the note is from.  This is used when matching
 *             NoteOff events with the corresponding NoteOn event.
 *             The channels for the NoteOn and NoteOff events must be
 *             the same.
 * notenumber - The note number, from 0 to 127.  Middle C is 60.
 * duration  - The time duration (measured in pulses) after which the 
 *             note is released.
 *
 * A MidiNote is created when we encounter a NoteOff event.  The duration
 * is initially unknown (set to 0).  When the corresponding NoteOff event
 * is found, the duration is set by the method NoteOff().
 */
public class MidiNote implements Comparator<MidiNote> {
	public int starttime;   /** The start time, in pulses */
	public int channel;     /** The channel */
    public int notenumber;  /** The note, from 0 to 127. Middle C is 60 */
    public int duration;    /** The duration, in pulses */
    public int m_nAlter;    //add by Torres //1 # -1 b...
    public int m_nAccidental; //none 0, nature 1, flat 2, sharp 3
    int velocity;
    private int m_nStem;	//尾巴朝向 1 up 2 down
    private int m_nFingering;//指法
    private boolean bFlagHasGrace;
    private MidiNote noteGrace;
    public int ifinger;		// 指法

    int m_nTotalCurrentTrackNoteIndex;
    boolean m_bArpeggiate;//琶音
    boolean m_bParentheses;//升音降音有没有括号
    int m_nArticulationsType; // 断音类型

    
    int m_nMeasureIndex;//小节索引
    int m_nTupletNormalType;
    
    int m_nDynamicsDirection;
	int m_nDynamicsTypes;
	
	public int m_nWordDirection;//上下
	public String m_szWordString;//文字
	
	public boolean m_bPedalStart;	//踏板开始标志
	public boolean m_bPedalStop;	//踏板结束标志
	
	public int m_nFermata;	//延音记号 1 上  2下
	public int m_nMordentDirection;	//下波音 1下 2上
	public int m_nInvertedMordentDirection;	//上波音 1下 2上
	public int m_nTurnDirection;	//回音 1下 2上
		
	public int m_nTempoDirection;
	public boolean m_bTempoParentheses;	 	//是否有括号
	public int m_nBeatUnit;				//节拍单元
	public int m_nPerMinute;			//一分钟几个拍
	
	public boolean m_bTrillMark;	//颤音标记
	
	public int m_nSegnoSymbolDirection;//带斜线的S
	public int m_nCodaSymbolDirection;//0加一个十字架

	private int starttimeMidi; 
	private int durationMidi;
	
	public int m_nArticulationsLocation;//断音等符号的位置
	public int m_nFingeringLocation;	//指法位置信息
	
	public boolean m_bAccent;//重音
	
    
    public int getFinger() {
		return m_nFingering;
	}
    
    
    public boolean getFlagHasGrace() {
		return bFlagHasGrace;
	}
    
    public MidiNote getNoteGrace() {
		return noteGrace;
	}
    
    public int getAlter()
    {
    	return m_nAlter;
    }
    
    public int getAccidental() {
		return m_nAccidental;
	}
    
    public int getStem() {
		return m_nStem;
	}

    public MidiNote(int starttime, int channel, int notenumber, int duration, int velocity)
    {
    	this.starttime = starttime;
    	this.channel = channel;
    	this.notenumber = notenumber;
    	this.duration = duration;
    	this.velocity = velocity;
    }
    /* Create a new MidiNote.  This is called when a NoteOn event is
     * encountered in the MidiFile.
     */
    public MidiNote(int starttime, int channel, int notenumber, int duration, MidiNote noteGrace,
    					boolean bFlagHasNoteGrace,int nTupletNormalType, int starttimemidi, int durationMidi)
    {
//        this.starttime = starttime;
//        this.channel = channel;
//        this.notenumber = notenumber;
//        this.duration = duration;
//        this.m_nAlter = notecell.m_nAlter;
//        this.m_nAccidental = notecell.m_nAccidental;
//        this.m_nStem = notecell.m_nStem;
//        this.m_slur = notecell.m_slur;
//        this.m_nFingering = notecell.m_nFingering;
//       	this.bFlagHasGrace = bFlagHasNoteGrace;
//        if(bFlagHasNoteGrace)
//        	this.noteGrace = noteGrace;
//        else 
//			this.noteGrace = null;
//        this.m_tie = notecell.m_tie;
//        this.m_nTotalCurrentTrackNoteIndex = notecell.m_nTotalCurrentTrackSortNoteIndex;
//        this.m_bArpeggiate = notecell.m_bArpeggiate;
//        this.m_bParentheses = notecell.m_bParentheses;
//        this.m_nDynamicsDirection = notecell.m_nDynamicsDirection;
//        this.m_nDynamicsTypes = notecell.m_nDynamicsTypes;
//        this.m_nWordDirection = notecell.m_nWordDirection;
//        this.m_szWordString = notecell.m_szWordString;
//        this.m_nMeasureIndex = notecell.m_nMeasureIndex;
//        this.m_nArticulationsType = notecell.m_nArticulationsType;
//        this.m_wedge = notecell.m_wedge;
//        this.m_nTupletNormalType = nTupletNormalType;
//        this.m_tuplet = notecell.m_tuplet;
//        this.m_nFermata = notecell.m_nFermata;
//        this.m_bPedalStart = notecell.m_bPedalStart;
//        this.m_bPedalStop = notecell.m_bPedalStop;
//		this.m_nMordentDirection = notecell.m_nMordentDirection;	//下波音
//		this.m_nInvertedMordentDirection = notecell.m_nInvertedMordentDirection;	//上波音
//		this.m_nTurnDirection = notecell.m_nTurnDirection;
//		this.m_bTempoParentheses = notecell.m_bTempoParentheses;	 	//是否有括号
//		this.m_nBeatUnit = notecell.m_nBeatUnit;				//节拍单元
//		this.m_nPerMinute = notecell.m_nPerMinute;			//一分钟几个拍
//		this.m_nTempoDirection = notecell.m_nTempoDirection;
//		this.m_bTrillMark = notecell.m_bTrillMark;
//		this.m_pWavyline = notecell.m_pWavyline;
//		this.m_octaveShift = notecell.m_octaveShift;
//		this.m_nSegnoSymbolDirection = notecell.m_nSegnoSymbolDirection;
//		this.m_nCodaSymbolDirection = notecell.m_nCodaSymbolDirection;
//		this.starttimeMidi = starttimemidi;
//		this.durationMidi = durationMidi;
//		this.m_nArticulationsLocation = notecell.m_nArticulationsLocation;
//		this.m_nFingeringLocation = notecell.m_nFingeringLocation;
//		this.velocity = 127;
    }


    public int getStartTime() { return starttime; }
    public void setStartTime(int value) { starttime = value; }
    
    public int getStartTimeMidi() { return starttimeMidi; }
    public void setStartTimeMidi(int value) { starttimeMidi = value; }
    
    public int getDurationMidi() { return durationMidi; }
    public void setDurationMidi(int value) { durationMidi = value; }
    public int getEndTimeMidi() { return starttimeMidi + durationMidi; }

    public int getEndTime() { return starttime + duration; }

    public int getChannel() { return channel; }
    public void setChannel(int value) { channel = value; }
    
    public void setVelocity(int velocity) {	this.velocity = velocity;}
    public int getVelocity() {return this.velocity;	}

    public int getNumber() { return notenumber; }
    public void setNumber(int value) { notenumber = value; }

    public int getDuration() { return duration; }
    public void setDuration(int value) { duration = value; }

    /* A NoteOff event occurs for this note at the given time.
     * Calculate the note duration based on the noteoff event.
     */
    public void NoteOff(int endtime) {
        duration = endtime - starttime;
    }

    /** Compare two MidiNotes based on their start times.
     *  If the start times are equal, compare by their numbers.
     */
    public int compare(MidiNote x, MidiNote y) {
        if (x.getStartTime() == y.getStartTime())
            return x.getNumber() - y.getNumber();
        else
            return x.getStartTime() - y.getStartTime();
    }


    public MidiNote Clone() {
        return new MidiNote(starttime, channel, notenumber, duration, velocity);
//        		MidiNote(noteSelfNote, starttime, channel, notenumber, duration,
//        		noteGrace, bFlagHasGrace,  m_nTupletNormalType, starttimeMidi, durationMidi);
    }

    @Override
    public 
    String toString() {
        String[] scale = new String[]{ "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" };
        return String.format("MidiNote channel=%1$s number=%2$s %3$s start=%4$s duration=%5$s",
                             channel, notenumber, scale[(notenumber + 3) % 12], starttime, duration);

    }

}


