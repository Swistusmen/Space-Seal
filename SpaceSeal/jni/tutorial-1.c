#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <gst/gst.h>

/*
 * Java Bindings
 */

//just a method which does some actions in cpp
static jstring
gst_native_get_gstreamer_info (JNIEnv * env, jobject thiz)
{
  char *version_utf8 = gst_version_string ();
  jstring *version_jstring = (*env)->NewStringUTF (env, version_utf8);
  g_free (version_utf8);
  return version_jstring;
}

//all c/cpp methos should be placed within this table
static JNINativeMethod native_methods[] = {
  {"nativeGetGStreamerInfo", "()Ljava/lang/String;",
      (void *) gst_native_get_gstreamer_info}
};

//runs everytime java loads library
jint
JNI_OnLoad (JavaVM * vm, void *reserved)
{
  JNIEnv *env = NULL;
  //check whether it is possible to get java env,which enables calling native methods
  if ((*vm)->GetEnv (vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
        "Could not retrieve JNIEnv");
    return 0;
  }
  //loading ui-> this is specific for the tutorial from gstreamer
  jclass klass = (*env)->FindClass (env,
      "org/freedesktop/gstreamer/tutorials/application/MainActivity");
  //registering methods within
  (*env)->RegisterNatives (env, klass, native_methods,
      G_N_ELEMENTS (native_methods));

  return JNI_VERSION_1_4;
}
