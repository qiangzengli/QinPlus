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

import java.util.Comparator;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :MidiEvent
 *             A MidiEvent represents a single event (such as EventNoteOn) in the
 *             Midi file. It includes the delta time of the event.
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MidiEvent implements Comparator<MidiEvent> {

    public int deltaTime;
    /**
     * The time between the previous event and this on
     */
    public int startTime;
    /**
     * The absolute time this event occurs
     */
    public boolean hasEventful;
    /**
     * False if this is using the previous eventflag
     */
    public byte eventFlag;
    /**
     * NoteOn, NoteOff, etc.  Full list is in class MidiFile
     */
    public byte channel;
    /**
     * The channel this event occurs on
     */

    public byte notenumber;
    /**
     * The note number
     */
    public byte velocity;
    /**
     * The volume of the note
     */
    public byte instrument;
    /**
     * The instrument
     */
    public byte keyPressure;
    /**
     * The key pressure
     */
    public byte chanPressure;
    /**
     * The channel pressure
     */
    public byte controlNum;
    /**
     * The controller number
     */
    public byte controlValue;
    /**
     * The controller value
     */
    public short pitchBend;
    /**
     * The pitch bend value
     */
    public byte numerator;
    /**
     * The numerator, for TimeSignature meta events
     */
    public byte denominator;
    /**
     * The denominator, for TimeSignature meta events
     */
    public int tempo;
    /**
     * The tempo, for tempo meta events
     */
    public byte metaevent;
    /**
     * The metaevent, used if eventflag is MetaEvent
     */
    public int metaLength;
    /**
     * The metaevent length
     */
    public byte[] value;

    /**
     * The raw byte value, for Sysex and meta events
     */

    public MidiEvent() {
    }

    /**
     * Return a copy of this event
     */
    @Override
    public MidiEvent clone() {
        MidiEvent maven = new MidiEvent();
        maven.deltaTime = deltaTime;
        maven.startTime = startTime;
        maven.hasEventful = hasEventful;
        maven.eventFlag = eventFlag;
        maven.channel = channel;
        maven.notenumber = notenumber;
        maven.velocity = velocity;
        maven.instrument = instrument;
        maven.keyPressure = keyPressure;
        maven.chanPressure = chanPressure;
        maven.controlNum = controlNum;
        maven.controlValue = controlValue;
        maven.pitchBend = pitchBend;
        maven.numerator = numerator;
        maven.denominator = denominator;
        maven.tempo = tempo;
        maven.metaevent = metaevent;
        maven.metaLength = metaLength;
        maven.value = value;
        return maven;
    }

    /**
     * Compare two MidiEvents based on their start times.
     */
    @Override
    public int compare(MidiEvent x, MidiEvent y) {
        if (x.startTime == y.startTime) {
            if (x.eventFlag == y.eventFlag) {
                return x.notenumber - y.notenumber;
            } else {
                return x.eventFlag - y.eventFlag;
            }
        } else {
            return x.startTime - y.startTime;
        }
    }

}


