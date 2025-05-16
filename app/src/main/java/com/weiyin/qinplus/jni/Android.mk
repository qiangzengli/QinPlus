LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS :=-llog
LOCAL_MODULE    := WriteMidiFile
LOCAL_SRC_FILES := MidiJni.c
LOCAL_SRC_FILES +=_BufferRW.c
LOCAL_SRC_FILES +=_MidiFileRW.c
include $(BUILD_SHARED_LIBRARY)