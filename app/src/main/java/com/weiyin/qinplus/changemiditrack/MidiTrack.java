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


package com.weiyin.qinplus.changemiditrack;

import java.util.ArrayList;


/**
 * @class MidiTrack
 * The MidiTrack takes as input the raw MidiEvents for the track, and gets:
 * - The list of midi notes in the track.
 * - The first instrument used in the track.
 * <p>
 * For each NoteOn event in the midi file, a new MidiNote is created
 * and added to the track, using the AddNote() method.
 * <p>
 * The NoteOff() method is called when a NoteOff event is encountered,
 * in order to update the duration of the MidiNote.
 */
public class MidiTrack {
    private int tracknum;
    /**
     * The track number
     */
    private int tracknumNew;
    private ArrayList<MidiNote> notes;
    /**
     * List of Midi notes
     */
    private ArrayList<MidiNote> notesNew;
    private int instrument;
    /**
     * instrument for this track
     */
    private ArrayList<MidiEvent> lyrics;
    /**
     * The lyrics in this track
     */
    private static final String TAG = "MidiTrack";

    /**
     * Create an empty MidiTrack.  Used by the clone method
     */
    public MidiTrack(int tracknum) {
        this.tracknum = tracknum;
        notes = new ArrayList<MidiNote>(20);
        instrument = 0;
    }

    /**
     * Create a MidiTrack based on the Midi events.  Extract the NoteOn/NoteOff
     * events to gather the list of MidiNotes.
     */
    public MidiTrack(ArrayList<MidiEvent> events, int tracknum) {
//        this.tracknumNew = tracknum;
//        notes = new ArrayList<MidiNote>(events.size());
//        //instrument = 0;
// 
//        for (MidiEvent mevent : events) {
//            if (mevent.eventFlag == MidiFile.EventNoteOn && mevent.velocity > 0) {
//                MidiNote note = new MidiNote(mevent.startTime, mevent.channel, mevent.notenumber, 0, mevent.velocity);
//                AddNote(note);
//            }
//            else if (mevent.eventFlag == MidiFile.EventNoteOn && mevent.velocity == 0) {
//                NoteOff(mevent.channel, mevent.notenumber, mevent.startTime);
//            }
//            else if (mevent.eventFlag == MidiFile.EventNoteOff) {
//                NoteOff(mevent.channel, mevent.notenumber, mevent.startTime);
//            }
////            else if (mevent.eventFlag == MidiFile.EventProgramChange) {
////                instrument = mevent.instrument;
////            }
////            else if (mevent.metaevent == MidiFile.MetaEventLyric) {
////                AddLyric(mevent);
////                if (lyrics == null) {
////                    lyrics = new ArrayList<MidiEvent>();
////                }
////                lyrics.add(mevent);
////            }
//        }
////        if (notes.size() > 0 && notes.get(0).getChannel() == 9)  {
////            instrument = 128;  /* Percussion */
////        }


        this.tracknum = tracknum;
        notes = new ArrayList<MidiNote>(events.size());
        instrument = 0;

        for (MidiEvent mevent : events) {
            if (mevent.eventFlag == MidiFile.EventNoteOn && mevent.velocity > 0) {
                MidiNote note = new MidiNote(mevent.startTime, mevent.channel, mevent.notenumber, 0,
                        mevent.velocity);
                AddNote(note);
            } else if (mevent.eventFlag == MidiFile.EventNoteOn && mevent.velocity == 0) {
                NoteOff(mevent.channel, mevent.notenumber, mevent.startTime);
            } else if (mevent.eventFlag == MidiFile.EventNoteOff) {
                NoteOff(mevent.channel, mevent.notenumber, mevent.startTime);
            } else if (mevent.eventFlag == MidiFile.EventProgramChange) {
                instrument = mevent.instrument;
            } else if (mevent.metaevent == MidiFile.MetaEventLyric) {
                AddLyric(mevent);
                if (lyrics == null) {
                    lyrics = new ArrayList<MidiEvent>();
                }
                lyrics.add(mevent);
            }
        }
        if (notes.size() > 0 && notes.get(0).getChannel() == 9) {
            instrument = 128;  /* Percussion */
        }
    }

//    public MidiTrack(MidiFile midiFile,ArrayList<Note> notesToatal, int tracknum, Measure measureinfo) {
//        this.tracknum = tracknum;
//        instrument = 0;
//        notes = new ArrayList<MidiNote>(notesToatal.size());
//        boolean bFlagGrace = false;//看是否是修饰�?
//        boolean bFlagHasGrace = false;
//        MidiNote noteGraceMidiNote = null;   
//        int quarterone = midiFile.getQuarterone();
//    	int duration = 0;//每个音符占的时间
//    	int offset = 0;//转换用�?�转换到五线谱上
//    	Tuplet tuplet = null;
//    	int iTupletNum = 0;
//    	int iTupletSum = 0;
//    	
//        for (Note notecell : notesToatal) { 
//        	if(notecell.m_nStep == Note.NoteStep_Rest)
//        		continue;
//        	//Torres test
//        	//notecell.m_nFingering = 2;
//        	//notecell.m_bArpeggiate = true;
//        	//notecell.m_nDynamicsDirection = tracknum + 1;
//        	//notecell.m_szWordString = "test";
//        	//notecell.m_nFermata = 2;
//        	//test end
//        	if(notecell.m_tuplet != null)
//        	{
//        		tuplet = notecell.m_tuplet;
//        		iTupletNum = tuplet.m_nActualNotes;
//        		iTupletSum = tuplet.m_nActualNotes;
//        	}
//        	    	
//        	if(notecell.m_bGrace)
//        	{
//        		bFlagGrace = true;	
//                switch (notecell.m_nStep) {
//		            case 1: offset = NoteScale.C; break;
//		            case 2: offset = NoteScale.D; break;
//		            case 3: offset = NoteScale.E; break;
//		            case 4: offset = NoteScale.F; break;
//		            case 5: offset = NoteScale.G; break;
//		            case 6: offset = 12; break;
//		            case 7: offset = 14; break;
//		            default: offset = 0; break;
//                }
//        		
//        		noteGraceMidiNote = new MidiNote(notecell,
//        									notecell.m_nTicksCountOffset * quarterone / 32, 
//						        			0, 
//						        			notecell.m_nOctave * 12 + offset + 9,
//						        			midiFile.getQuarterone() * 4 / notecell.m_nType,
//						        			null,
//						        			false,
//						        			0,
//						        			0,
//						        			0);
//        		continue;
//        	}   	
//        			
//        	if(bFlagGrace)
//        	{
//        		bFlagHasGrace = true;
//        		bFlagGrace = false;
//        	}
//    	
//        	duration = midiFile.getQuarterone() * 4 / notecell.m_nType;     	
//        	if(notecell.m_bDot)//如果是附加点 �?要增加一半的时间
//        	{	
//        		duration += duration / 2;     	
//        	}
//        	if(tuplet != null)
//        	{
//        		//duration = midiFile.getQuarterone() * tuplet.m_nNormalNotes / tuplet.m_nActualNotes;        
//        		duration = midiFile.getQuarterone() * 4 / tuplet.m_nNormalType  - 50;   
//        	}
//
//            switch (notecell.m_nStep) {
//	            case 1: offset = NoteScale.C; break;
//	            case 2: offset = NoteScale.D; break;
//	            case 3: offset = NoteScale.E; break;
//	            case 4: offset = NoteScale.F; break;
//	            case 5: offset = NoteScale.G; break;
//	            case 6: offset = 12; break;
//	            case 7: offset = 14; break;
//	            default: offset = 0; break;
//            }
//            
//            boolean bflag = false;
//            if(bFlagHasGrace)
//            {
//            	bflag = true;
//            	bFlagHasGrace = false;
//            }
//            else {
//            	noteGraceMidiNote = null;
//            	bFlagHasGrace = false;
//            	bflag = false;
//			}
//            
//        	if(iTupletNum > 0)
//        	{
//        		MidiNote note = new MidiNote(notecell,
//        				notecell.m_nTicksCountOffset * quarterone / 32 + (iTupletSum - iTupletNum) * duration, 
//	        			0, 
//	        			notecell.m_nOctave * 12 + offset + 9,
//	        			0,
//	        			noteGraceMidiNote,
//	        			bflag,
//	        			tuplet.m_nNormalType,
//	        			0,
//	        			0);
//				AddNote(note);
//				NoteOff(0, notecell.m_nOctave * 12 + offset + 9,       		
//						notecell.m_nTicksCountOffset * quarterone / 32 + (iTupletSum - iTupletNum) * duration + duration);
//				iTupletNum --;
//				if(iTupletNum < 1)
//					tuplet = null;
//        	}
//        	else {	       	
//        		tuplet = null;
//	        	MidiNote note = new MidiNote(notecell,
//	        								notecell.m_nTicksCountOffset * quarterone / 32, 
//							        		0, 
//							        		notecell.m_nOctave * 12 + offset + 9,
//							        		0,
//							        		noteGraceMidiNote,
//							        		bflag,
//							        		0,
//							        		0,
//							        		0);
//	        	AddNote(note);
//	        	NoteOff(0, 
//		        		notecell.m_nOctave * 12 + offset + 9,       		
//		        		notecell.m_nTicksCountOffset * quarterone / 32 + duration);
//        	}
//        }
//    }

    public int trackNumber() {
        return tracknum;
    }

    public ArrayList<MidiNote> getNotes() {
        return notes;
    }

    public ArrayList<MidiNote> getNotesNew() {
        return notesNew;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int value) {
        instrument = value;
    }

    public ArrayList<MidiEvent> getLyrics() {
        return lyrics;
    }

    public void setLyrics(ArrayList<MidiEvent> value) {
        lyrics = value;
    }


    public String getInstrumentName() {
        if (instrument >= 0 && instrument <= 128)
            return MidiFile.Instruments[instrument];
        else
            return "";
    }

    /**
     * Add a MidiNote to this track.  This is called for each NoteOn event
     */
    public void AddNote(MidiNote m) {
        notes.add(m);
    }

    public void AddNoteNew(MidiNote m) {
        notesNew.add(m);
    }

    /**
     * A NoteOff event occured.  Find the MidiNote of the corresponding
     * NoteOn event, and update the duration of the MidiNote.
     */
    public void NoteOff(int channel, int notenumber, int endtime) {
        for (int i = notes.size() - 1; i >= 0; i--) {
            MidiNote note = notes.get(i);
            if (note.getChannel() == channel && note.getNumber() == notenumber &&
                    note.getDuration() == 0) {
                note.NoteOff(endtime);
                return;
            }
        }
    }

    public void NoteOffNew(int channel, int notenumber, int endtime) {
        for (int i = notesNew.size() - 1; i >= 0; i--) {
            MidiNote note = notesNew.get(i);
            if (note.getChannel() == channel && note.getNumber() == notenumber &&
                    note.getDuration() == 0) {
                note.NoteOff(endtime);
                return;
            }
        }
    }

    /**
     * Add a lyric event to this track
     */
    public void AddLyric(MidiEvent mevent) {
        if (lyrics == null) {
            lyrics = new ArrayList<MidiEvent>();
        }
        lyrics.add(mevent);
    }

    /**
     * Return a deep copy clone of this MidiTrack.
     */
    public MidiTrack Clone() {
        MidiTrack track = new MidiTrack(trackNumber());
        track.instrument = instrument;
        for (MidiNote note : notes) {
            track.notes.add(note.Clone());
        }
        if (lyrics != null) {
            track.lyrics = new ArrayList<MidiEvent>();
            for (MidiEvent ev : lyrics) {
                track.lyrics.add(ev);
            }
        }
        return track;
    }

    @Override
    public String toString() {
        String result = "Track number=" + tracknum + " instrument=" + instrument + "\n";
        for (MidiNote n : notes) {
            result = result + n + "\n";
        }
        result += "End Track\n";
        return result;
    }
}


