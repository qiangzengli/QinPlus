package com.weiyin.qinplus.usb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.ui.tv.bwstaff.KeyBoardModule;
import com.weiyin.qinplus.usb.device.Tone;
import com.weiyin.qinplus.usb.listener.UsbConnectListener;
import com.weiyin.qinplus.usb.listener.UsbDataListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

/**
 * Created by lenovo on 2017/10/30.
 */

public class UsbController {
    public static final String TAG = UsbController.class.getSimpleName();

    private Activity mContext;
    @SuppressLint("StaticFieldLeak")
    private static UsbController instance;
    private Set<UsbDevice> usbDevices = new HashSet<UsbDevice>();
    // the driver
    private UsbMidiDriver usbMidiDriver;
    private ArrayList<UsbDevice> connectedDevicesAdapter = null;
    int currentProgram = 0;
    final Set<Tone> tones = new HashSet<Tone>();
    private List<UsbConnectListener> usbConnectListenerList;
    private UsbDataListener usbDataListener;
    private boolean mConnected = false;

    public boolean ismConnected() {
        return mConnected;
    }

    public UsbMidiDriver getUsbMidiDriver() {
        return usbMidiDriver;
    }

    public Set<UsbDevice> getUsbDevices() {
        return usbDevices;
    }

    public void setUsbDataListener(UsbDataListener usbDataListener) {
        this.usbDataListener = usbDataListener;
    }

    public void addUsbConnectListener(UsbConnectListener usbConnectListener) {
        if (usbConnectListenerList != null) {
            this.usbConnectListenerList.add(usbConnectListener);
        }
    }

    public void removeUsbConnectListener(UsbConnectListener usbConnectListener) {
        if (usbConnectListenerList != null) {
            if (usbConnectListenerList.contains(usbConnectListener)) {
                usbConnectListenerList.remove(usbConnectListener);
            }
        }
    }

    public ArrayList<UsbDevice> getConnectedDevicesAdapter() {
        return connectedDevicesAdapter;
    }

    public void removeUsbDataListener() {
        usbDataListener = null;
    }

    final Handler midiOutputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            LogUtil.i(TAG,"midiOutputEventHandler="+(String)msg.obj);
//            if (myAdapter != null) {
//                myAdapter.list.add((String) msg.obj);
//                myAdapter.notifyDataSetChanged();
//            }
            // message handled successfully
            return true;
        }
    });
    // User interface
    final Handler midiInputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (usbDataListener != null && msg.what == 1) {
                KeyBoardModule keyBoardModule = (KeyBoardModule) msg.obj;
                usbDataListener.usbData(keyBoardModule.getStatus(), keyBoardModule.getChannel(), keyBoardModule.getNumber(), keyBoardModule.getVelocity());
            }
//            if (myAdapter != null) {
//                myAdapter.list.add((String) msg.obj);
//                myAdapter.notifyDataSetChanged();
//            }
            // message handled successfully
            return true;
        }
    });
    Handler mHandler = new Handler();

    /**
     * Choose device from spinner
     *
     * @return the MidiOutputDevice from spinner
     */
    @Nullable
    public MidiOutputDevice getMidiOutputDeviceFromSpinner() {
        for (int i = 0; i < connectedDevicesAdapter.size(); i++) {
            UsbDevice device = connectedDevicesAdapter.get(i);
            if (device != null) {
                Set<MidiOutputDevice> midiOutputDevices = usbMidiDriver.getMidiOutputDevices(device);

                if (midiOutputDevices.size() > 0) {
                    // returns the first one.
                    return (MidiOutputDevice) midiOutputDevices.toArray()[0];
                }
            } else {
                LogUtil.i(TAG, "connectedDevicesAdapter = null");
            }
        }
        return null;
    }

    public static UsbController getUsbController() {
        if (instance == null) {
            instance = new UsbController();
        }
        return instance;
    }

    private UsbController() {

    }

    public void init(Activity context) {
        this.mContext = context;
        usbConnectListenerList = new ArrayList<>();
        connectedDevicesAdapter = new ArrayList<>();
        usbMidiDriver = new UsbMidiDriver(context) {
            @Override
            public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
                LogUtil.i(TAG, "onDeviceAttached");
                // deprecated method.
                // do nothing
            }

            @Override
            public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
                LogUtil.i(TAG, "onMidiInputDeviceAttached");
            }

            @Override
            public void onMidiOutputDeviceAttached(@NonNull final MidiOutputDevice midiOutputDevice) {
                LogUtil.i(TAG, "onMidiOutputDeviceAttached");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnected = true;
                        if (connectedDevicesAdapter != null) {
                            connectedDevicesAdapter.remove(midiOutputDevice.getUsbDevice());
                            connectedDevicesAdapter.add(midiOutputDevice.getUsbDevice());
                        }
                        BlueToothControl blueToothControl = BlueToothControl.getBlueToothInstance();
                        if (blueToothControl.getConnectFlag()) {
                            blueToothControl.disConnect();
                        }
                        if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                            for (int i = 0; i < usbConnectListenerList.size(); i++) {
                                usbConnectListenerList.get(i).result(0);
                                ToastUtil.showTextToast(mContext, "USB已连接");
                            }
                        }
                    }
                });
            }

            @Override
            public void onDeviceDetached(@NonNull UsbDevice usbDevice) {
                LogUtil.i(TAG, "onDeviceDetached");
                (mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnected = false;
                        ToastUtil.showTextToast(mContext, "USB断开连接");
                    }
                });

                // deprecated method.
                // do nothing
            }

            @Override
            public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
                LogUtil.i(TAG, "onMidiInputDeviceDetached");
            }

            @Override
            public void onMidiOutputDeviceDetached(@NonNull final MidiOutputDevice midiOutputDevice) {
                LogUtil.i(TAG, "onMidiOutputDeviceDetached");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectedDevicesAdapter != null) {
                            connectedDevicesAdapter.remove(midiOutputDevice.getUsbDevice());
                        }
                        if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                            for (int i = 0; i < usbConnectListenerList.size(); i++) {
                                usbConnectListenerList.get(i).result(1);
                            }
                        }
//                            connectedDevicesAdapter.remove(midiOutputDevice.getUsbDevice());
//                            connectedDevicesAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onMidiNoteOff(@NonNull final MidiInputDevice sender, int cable, int channel, int note, int velocity) {
                LogUtil.i(TAG, "NoteOff from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", velocity: " + velocity);
//                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOff from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", velocity: " + velocity));
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 1, new KeyBoardModule(note, 80, velocity, channel)));
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiNoteOff(cable, channel, note, velocity);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "NoteOff from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", velocity: " + velocity));
//                }
//                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
//                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
//                        usbConnectListenerList.get(i).resultString("8" + channel + " " + (Integer.parseInt(note + "", 16)) + " " + (Integer.parseInt(velocity + "", 16)));
//                    }
//                }
//                if (usbDataListener != null) {
//                    usbDataListener.usbData(80, channel, note, velocity);
//                }
                //80
                synchronized (tones) {
                    Iterator<Tone> it = tones.iterator();
                    while (it.hasNext()) {
                        Tone tone = it.next();
                        if (tone.getNote() == note) {
                            it.remove();
                        }
                    }
                }
            }

            @Override
            public void onMidiNoteOn(@NonNull final MidiInputDevice sender, int cable, int channel, int note, int velocity) {
                LogUtil.i(TAG, "NoteOn from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ",  channel: " + channel + ", note: " + note + ", velocity: " + velocity);
//                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOn from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ",  channel: " + channel + ", note: " + note + ", velocity: " + velocity));
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 1, new KeyBoardModule(note, 90, velocity, channel)));
                //90
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiNoteOn(cable, channel, note, velocity);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "NoteOn from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ",  channel: " + channel + ", note: " + note + ", velocity: " + velocity));
//                }
//                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
//                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
//                        usbConnectListenerList.get(i).resultString("9" + channel + " " + (Integer.parseInt(note + "", 16)) + " " + Integer.parseInt(velocity + "", 16));
//                    }
//                }
//                if (usbDataListener != null) {
//                    usbDataListener.usbData(90, channel, note, velocity);
//                }


                synchronized (tones) {
                    if (velocity == 0) {
                        Iterator<Tone> it = tones.iterator();
                        while (it.hasNext()) {
                            Tone tone = it.next();
                            if (tone.getNote() == note) {
                                it.remove();
                            }
                        }
                    } else {
                        tones.add(new Tone(note, velocity / 127.0, currentProgram));
                    }
                }
            }

            @Override
            public void onMidiPolyphonicAftertouch(@NonNull final MidiInputDevice sender, int cable, int channel, int note, int pressure) {
                LogUtil.i(TAG, "onMidiPolyphonicAftertouch");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PolyphonicAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", pressure: " + pressure));
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(channel + " " + note + " " + pressure);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiPolyphonicAftertouch(cable, channel, note, pressure);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "PolyphonicAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", pressure: " + pressure));
//                }
            }

            @Override
            public void onMidiControlChange(@NonNull final MidiInputDevice sender, int cable, int channel, int function, int value) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ControlChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", function: " + function + ", value: " + value));
                LogUtil.i(TAG, "onMidiControlChange");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(channel + " " + function + " " + value);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiControlChange(cable, channel, function, value);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "ControlChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", function: " + function + ", value: " + value));
//                }
            }

            @Override
            public void onMidiProgramChange(@NonNull final MidiInputDevice sender, int cable, int channel, int program) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ProgramChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", program: " + program));
                LogUtil.i(TAG, "onMidiProgramChange");
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiProgramChange(cable, channel, program);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "ProgramChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", program: " + program));
//                }
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(channel + " " + program);
                    }
                }
                currentProgram = program % Tone.FORM_MAX;
                synchronized (tones) {
                    for (Tone tone : tones) {
                        tone.setForm(currentProgram);
                    }
                }
            }

            @Override
            public void onMidiChannelAftertouch(@NonNull final MidiInputDevice sender, int cable, int channel, int pressure) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ChannelAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", pressure: " + pressure));
                LogUtil.i(TAG, "onMidiChannelAftertouch");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(channel + " " + pressure);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiChannelAftertouch(cable, channel, pressure);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "ChannelAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", pressure: " + pressure));
//                }
            }

            @Override
            public void onMidiPitchWheel(@NonNull final MidiInputDevice sender, int cable, int channel, int amount) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PitchWheel from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", amount: " + amount));
                LogUtil.i(TAG, "onMidiPitchWheel");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(channel + " " + amount);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiPitchWheel(cable, channel, amount);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "PitchWheel from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", amount: " + amount));
//                }
            }

            @Override
            public void onMidiSystemExclusive(@NonNull final MidiInputDevice sender, int cable, final byte[] systemExclusive) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemExclusive from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data:" + Arrays.toString(systemExclusive)));
                StringBuilder stringBuilder1 = new StringBuilder(systemExclusive.length);
                for (byte byteChar : systemExclusive) {
                    stringBuilder1.append(String.format("%02X ", byteChar));
                    if (String.format("%02X ", byteChar).contains("F7")) {
                        break;
                    }
                }
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(stringBuilder1.toString());
                    }
                }
//                StringBuilder stringBuilder = new StringBuilder(systemExclusive.length);
//                for (int i = 0; i < stringBuilder1.length(); i += 3) {
//                    String s = stringBuilder1.substring(i, i + 3);
//                    if (s.contains("04")) {
//                        stringBuilder.append(stringBuilder1.substring(i + 3, i + 12));
//                        i += 9;
//                    } else if (s.contains("05") || s.contains("06") || s.contains("07")) {
//                        stringBuilder.append(stringBuilder1.substring(i + 3));

//                    }
//                }
                LogUtil.i(TAG, "onMidiSystemExclusive=" + "SystemExclusive from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data:" + Arrays.toString(systemExclusive) + " stringBuilder=" + stringBuilder1.toString());
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiSystemExclusive(cable, systemExclusive);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "SystemExclusive from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data:" + Arrays.toString(systemExclusive)));
//                }
            }

            @Override
            public void onMidiSystemCommonMessage(@NonNull final MidiInputDevice sender, int cable, final byte[] bytes) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemCommonMessage from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", bytes: " + Arrays.toString(bytes)));
                StringBuilder stringBuilder1 = new StringBuilder(bytes.length);
                for (byte byteChar : bytes) {
                    stringBuilder1.append(String.format("%02X ", byteChar));
                    if (String.format("%02X ", byteChar).contains("F7")) {
                        break;
                    }
                }
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(stringBuilder1.toString());
                    }
                }
                LogUtil.i(TAG, "onMidiSystemCommonMessage");
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiSystemCommonMessage(cable, bytes);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "SystemCommonMessage from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", bytes: " + Arrays.toString(bytes)));
//                }
            }

            @Override
            public void onMidiSingleByte(@NonNull final MidiInputDevice sender, int cable, int byte1) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SingleByte from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data: " + byte1));
                LogUtil.i(TAG, "onMidiSingleByte");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(byte1 + "");
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiSingleByte(cable, byte1);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "SingleByte from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data: " + byte1));
//                }
            }

            @Override
            public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int cable, int timing) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimeCodeQuarterFrame from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", timing: " + timing));
                LogUtil.i(TAG, "onMidiTimeCodeQuarterFrame");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(timing + "");
                    }
                }
            }

            @Override
            public void onMidiSongSelect(@NonNull MidiInputDevice sender, int cable, int song) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongSelect from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", song: " + song));
                LogUtil.i(TAG, "onMidiSongSelect");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(song + "");
                    }
                }
            }

            @Override
            public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int cable, int position) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongPositionPointer from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", position: " + position));
                LogUtil.i(TAG, "onMidiSongPositionPointer");
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(position + "");
                    }
                }
            }

            @Override
            public void onMidiTuneRequest(@NonNull MidiInputDevice sender, int cable) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TuneRequest from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
                LogUtil.i(TAG, "onMidiTuneRequest");
            }

            @Override
            public void onMidiTimingClock(@NonNull MidiInputDevice sender, int cable) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimingClock from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
                LogUtil.i(TAG, "onMidiTimingClock");
            }

            @Override
            public void onMidiStart(@NonNull MidiInputDevice sender, int cable) {
                LogUtil.i(TAG, "onMidiStart");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Start from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
            }

            @Override
            public void onMidiContinue(@NonNull MidiInputDevice sender, int cable) {
                LogUtil.i(TAG, "onMidiContinue");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Continue from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
            }

            @Override
            public void onMidiStop(@NonNull MidiInputDevice sender, int cable) {
                LogUtil.i(TAG, "onMidiStop");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Stop from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
            }

            @Override
            public void onMidiActiveSensing(@NonNull MidiInputDevice sender, int cable) {
                LogUtil.i(TAG, "onMidiActiveSensing");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ActiveSensing from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
            }

            @Override
            public void onMidiReset(@NonNull MidiInputDevice sender, int cable) {
                LogUtil.i(TAG, "onMidiReset");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Reset from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable));
            }

            @Override
            public void onMidiMiscellaneousFunctionCodes(@NonNull final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
                LogUtil.i(TAG, "onMidiMiscellaneousFunctionCodes");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "MiscellaneousFunctionCodes from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(byte1 + " " + byte2 + " " + byte3);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiMiscellaneousFunctionCodes(cable, byte1, byte2, byte3);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "MiscellaneousFunctionCodes from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));
//                }
            }

            @Override
            public void onMidiCableEvents(@NonNull final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
                LogUtil.i(TAG, "onMidiCableEvents");
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "CableEvents from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));
                if (usbConnectListenerList != null && usbConnectListenerList.size() > 0) {
                    for (int i = 0; i < usbConnectListenerList.size(); i++) {
                        usbConnectListenerList.get(i).resultString(byte1 + " " + byte2 + " " + byte3);
                    }
                }
//                if (getMidiOutputDeviceFromSpinner() != null) {
//                    getMidiOutputDeviceFromSpinner().sendMidiCableEvents(cable, byte1, byte2, byte3);
//                    midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "CableEvents from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));
//                }
            }
        };
        usbMidiDriver.open();
    }

    public void sendMidiOn(int cable, int channel, int note, int velocity) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            Log.i(TAG, channel + " " + note + " " + velocity);
            getMidiOutputDeviceFromSpinner().sendMidiNoteOn(cable, channel, note, velocity);
        } else {
            LogUtil.i(TAG, "NULL");
        }
    }

    public void sendMidiOff(int cable, int channel, int note, int velocity) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            Log.i(TAG, channel + " " + note + " " + velocity);
            getMidiOutputDeviceFromSpinner().sendMidiNoteOff(cable, channel, note, velocity);
        } else {
            LogUtil.i(TAG, "NULL");
        }
    }

    public void sendSystem(int cable, byte[] bytes) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            getMidiOutputDeviceFromSpinner().sendMidiSystemExclusive(cable, bytes);
        } else {
            LogUtil.i(TAG, "NULL");
        }
    }

    public void sendC(int cable, int channel, int program) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            getMidiOutputDeviceFromSpinner().sendMidiProgramChange(cable, channel, program);
        } else {
            LogUtil.i(TAG, "NULL");
        }
    }

    public void send1(int byte1, int byte2, int byte3) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            Log.i(TAG, byte1 + "====" + byte2 + "=====" + byte3);
            getMidiOutputDeviceFromSpinner().sendMidiMiscellaneousFunctionCodes(0, byte1, byte2, byte3);
        } else {
            LogUtil.i(TAG, "NULL");

        }
    }

    public void sendB(int channel, int function, int value) {
        if (getMidiOutputDeviceFromSpinner() != null) {
            Log.i(TAG, channel + "====" + function + "=====" + value);
            getMidiOutputDeviceFromSpinner().sendMidiControlChange(0, channel, function, value);
        } else {
            LogUtil.i(TAG, "NULL");
        }
    }
}
