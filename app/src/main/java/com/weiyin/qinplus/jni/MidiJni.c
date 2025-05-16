//
// Created by lenovo on 2016/10/11.
//
#include <jni.h>
#include "com_weiyin_letvpiano_ui_tv_bwstaff_DB_MidiJni.h"
#include "_MidiFileRW.h"
#include  <Android/log.h>
#define TAG "myhello-jni-test"//聽杩欎釜鏄嚜瀹氫箟鐨凩OG鐨勬爣璇喡犅�
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) //聽瀹氫箟LOGD绫诲瀷

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_weiyin_letvpiano_ui_tv_bwstaff_DB_MidiJni
 * Method:    WriteRecordedEvents
 * Signature: ([BLjava/util/ArrayList;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_weiyin_letvpiano_ui_tv_bwstaff_DB_MidiJni_WriteRecordedEvents
        (JNIEnv *env, jobject thiz, jbyteArray buff, jobject tagwevent)
{
    LOGI("##########i=%d",1);
    unsigned char *pbuffer = (*env)->GetByteArrayElements(env,buff,0);

    jclass cls_arraylist = (*env)->GetObjectClass(env,tagwevent);
    jmethodID arraylist_get = (*env)->GetMethodID(env, cls_arraylist,"get","(I)Ljava/lang/Object;");
    jmethodID arraylist_size = (*env)->GetMethodID(env, cls_arraylist,"size","()I");
    jint len = (*env)->CallIntMethod(env, tagwevent,arraylist_size);
    LOGI("##########i length=%d",len);

    int i = 0;
    jmethodID event_getticks;
    jmethodID event_getlen;
    jmethodID event_getdata;
    jfieldID fid_arrays;
    EVENT eventlist[len];
    unsigned char* mc = malloc(3 * sizeof(unsigned char) * len);
    EVENT event;
    if (len > 0)
    {
        jobject obj_even = (*env)->CallObjectMethod(env,tagwevent,arraylist_get,0);
        jclass cls_event = (*env)->GetObjectClass(env,obj_even);
        event_getticks = (*env)->GetMethodID(env,cls_event,"getTimeStamp","()I");
        event_getlen = (*env)->GetMethodID(env,cls_event,"getLen","()I");
        fid_arrays = (*env)->GetFieldID(env,cls_event,"data","[I");
    }
    int j = 0;
    for(i = 0 ; i < len; i ++)
    {
        LOGI("##########i length=%d",i);
        jobject obj_event = (*env)->CallObjectMethod(env,tagwevent,arraylist_get,i);
        jclass cls_event = (*env)->GetObjectClass(env,obj_event);

        jint tick = (*env)->CallIntMethod(env,obj_event,event_getticks);
        jint lendata = (*env)->CallIntMethod(env,obj_event,event_getlen);

        jintArray jint_arr = (jintArray)(*env)->GetObjectField(env,obj_event,fid_arrays);
        //获取arrays对象的指针
        jint* int_arr = (*env)->GetIntArrayElements(env,jint_arr,0);
        event.Len = lendata;
        event.TimeStamp = tick;

        event.Data = mc + j;
        int k;
        for( k  = 0; k < lendata; k ++)
        {
            mc[j] = int_arr[k];
            j++;
        }

        eventlist[i] = event;
        (*env)->DeleteLocalRef(env, obj_event);
        (*env)->DeleteLocalRef(env, cls_event);

        (*env)->DeleteLocalRef(env, jint_arr);
    }
    LOGI("########## finish == %d",1);
    int sizep;
    BYTE jchar1 =WriteRecordedEvents(&pbuffer,&sizep,eventlist,len);
    //BYTE jchar1 =WriteRecordedEventsIntoFile("\mnt\sdcard\testtesttest.mid",eventlist,len);
    LOGI("##########sizep=%d",sizep);

    jbyteArray jarrRV =(*env)->NewByteArray(env,sizep);//创建一个byte数组
//    jbyte *jby =(*env)->GetByteArrayElements(env,jarrRV,0);
//    memcpy(jby, pbuffer, sizep);
//    (*env)->SetByteArrayRegion(env,jarrRV,0,sizep, jby);
    (*env)->SetByteArrayRegion(env,jarrRV,0,sizep, pbuffer);
    (*env)->ReleaseByteArrayElements(env,buff,pbuffer,0);
//    int j = 0;
//    for(j = 0;j < (int)sizep; j++)
//    {
//    	EVENT event = eventlist[j];
//    	free(event.Data);
//    }
//    free(eventlist);
    free(mc);
    return jarrRV;
}

#ifdef __cplusplus
}
#endif
