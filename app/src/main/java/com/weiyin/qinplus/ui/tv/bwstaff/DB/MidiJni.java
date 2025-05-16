package com.weiyin.qinplus.ui.tv.bwstaff.DB;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/10/10.
 */

public class MidiJni {
    static {
        try{
            System.loadLibrary("WriteMidiFile");
        }catch (Exception e)
        {
            Log.i("123","加载失败");
        }
    }

    public native byte[] WriteRecordedEvents(byte[] buffer, ArrayList<TagEvent> tagEvents);
}
