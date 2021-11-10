#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <gst/gst.h>
#include <gst/rtsp-server/rtsp-server.h>
#include <gst/codecparsers/codecparsers-prelude.h>
#include <gstreamer-1.0/gst/codecparsers/gstav1parser.h>
#include <libavcodec/avcodec.h>
#include <pthread.h>
#include <glib.h>
#include <glibconfig.h>

#if GLIB_SIZEOF_VOID_P == 8
# define GET_CUSTOM_DATA(env, thiz, fieldID) (CustomData *)(*env)->GetLongField (env, thiz, fieldID)
# define SET_CUSTOM_DATA(env, thiz, fieldID, data) (*env)->SetLongField (env, thiz, fieldID, (jlong)data)
#else
# define GET_CUSTOM_DATA(env, thiz, fieldID) (CustomData *)(jint)(*env)->GetLongField (env, thiz, fieldID)
# define SET_CUSTOM_DATA(env, thiz, fieldID, data) (*env)->SetLongField (env, thiz, fieldID, (jlong)(jint)data)
#endif

static jfieldID custom_data_field_id;
static jfieldID streammingObject_id;
static jfieldID IpPath;
static jfieldID test;
static jmethodID java_method_that_can_be_called_from_here;
static jstring testString;
static jint testInt;
static jobject app;
static char *IP;
static char *Port;
static char *Path;
static char* pipelineDesc;
static char* Protocol;
static GstRTSPServer *server;
static GMainLoop *loop;
static GstRTSPMountPoints *mounts;
static GstRTSPMediaFactory *factory;
static pthread_t app_thread;
static GError* error;
static GstBus* bus;
static GstElement* pipeline;

static void
media_prepared_cb (GstRTSPMedia * media)
{
    guint i, n_streams;

    n_streams = gst_rtsp_media_n_streams (media);

    GST_INFO ("media %p is prepared and has %u streams", media, n_streams);

    for (i = 0; i < n_streams; i++) {
        GstRTSPStream *stream;
        GObject *session;

        stream = gst_rtsp_media_get_stream (media, i);
        if (stream == NULL)
            continue;

        session = gst_rtsp_stream_get_rtpsession (stream);
    }
}

static void
media_configure_cb (GstRTSPMediaFactory * factory, GstRTSPMedia * media)
{
    g_signal_connect (media, "prepared", (GCallback) media_prepared_cb, factory);
}

static void * main_function(void * userData){
    g_print("RTSP");
    GMainContext * context;
    context=g_main_context_new();
    g_main_context_push_thread_default(context);
    server=gst_rtsp_server_new();
    g_print("1");
    g_object_set (server, "service", "8554", NULL);
    g_print("2");
    mounts=gst_rtsp_server_get_mount_points(server);
    factory=gst_rtsp_media_factory_new();
    gst_rtsp_media_factory_set_launch (factory, pipelineDesc);
    g_signal_connect(factory, "media-configure", (GCallback) media_configure_cb , factory);
    g_print("3");
    gst_rtsp_mount_points_add_factory(mounts,"/test",factory);
    g_print("4");
    g_object_unref(mounts);

    if(gst_rtsp_server_attach (server, context)==0)
        __android_log_print (ANDROID_LOG_ERROR, "tutorial-1","failed to attach server");

    loop=g_main_loop_new(context,FALSE);

    g_main_loop_run(loop);
    g_main_loop_unref(loop);
}

static void* stream_http(void* userData){

    GMainContext * context;
    context=g_main_context_new();
    g_main_context_push_thread_default(context);

    loop=g_main_loop_new(context,FALSE);

    pipeline=gst_parse_launch_full (pipelineDesc, NULL, GST_PARSE_FLAG_FATAL_ERRORS, &error);
    if (!pipeline || error) {
        g_printerr ("Unable to build pipeline: %s", error->message ? error->message : "(no debug)");
    }
    gst_element_set_state(pipeline, GST_STATE_PLAYING);
    /*bus=gst_element_get_bus(pipeline);
    gst_bus_add_signal_watch(bus);
    g_signal_connect( G_OBJECT(bus),"message::eos",(GCallback)onEOSCallback,pipeline);
    gst_object_unref(bus);*/
    g_main_loop_run(loop);
}

static void * enable_embeeded_to_use_Java(JNIEnv* env, jclass klass){
    custom_data_field_id= (*env)->GetFieldID(env,klass,"native_custom_data","J");//enalbes C to use a field of the class,named 3rd arg
    streammingObject_id=(*env)->GetFieldID(env,klass,"nativeStreammingObject", "J");
    IpPath= (jstring)(*env)->GetFieldID(env,klass,"path", "Ljava/lang/String;");
    test= (*env)->GetFieldID(env,klass,"testInteger","Ljava/lang/Integer;");
}

static void * gst_native_init(JNIEnv* env, jobject thiz,jstring pipelineDescription, jstring protocol, jstring port,jstring path) //initalizes instance of class, to be able to call cfunctions
{
    app= (*env)->NewGlobalRef(env,thiz);

    pipelineDesc=(*env)->GetStringUTFChars(env,pipelineDescription,0);
    (*env)->ReleaseStringUTFChars(env,pipelineDescription,pipelineDesc);

    Port=(*env)->GetStringUTFChars(env,port,0);
    (*env)->ReleaseStringUTFChars(env,port,Port);

    Path=(*env)->GetStringUTFChars(env,path,0);
    (*env)->ReleaseStringUTFChars(env,path,Path);

    Protocol=(*env)->GetStringUTFChars(env,protocol,0);
    (*env)->ReleaseStringUTFChars(env,protocol,Protocol);

    char current_dir[FILENAME_MAX];
    getcwd(current_dir,FILENAME_MAX);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",current_dir);

    getcwd(current_dir,FILENAME_MAX);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",current_dir);

    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",Protocol);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",pipelineDesc);
    char* compar="RTSP";

    if(*Protocol==*compar) {
        pthread_create(&app_thread, NULL, &main_function, NULL);
    }
    else {
        chdir("storage/emulated/0/Movies/"); //Need to be changed to more general @TODO
        pthread_create(&app_thread, NULL, &stream_http, NULL);
    }

}

//just a method which does some actions in cpp
static jstring
gst_native_get_gstreamer_info (JNIEnv * env, jobject thiz)
{
  char *version_utf8 = gst_version_string ();
  jstring *version_jstring = (*env)->NewStringUTF (env, version_utf8);
  g_free (version_utf8);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",Path);
  return version_jstring;
}

static jstring gst_randomTextTest (JNIEnv* env, jobject thiz,jstring ip, jstring port,jstring path)
{
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",IP);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",pipelineDesc);
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1",
                         "%s",Port);
    g_print("Hello");
    return ip;
}

//all c/cpp methos should be placed within this table
static JNINativeMethod native_methods[] = {
  {"nativeGetGStreamerInfo", "()Ljava/lang/String;",(void *) gst_native_get_gstreamer_info},
  { "nativeInit", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",(void *) gst_native_init},
  {"nativeEnableCRunJava", "()V",(void *) enable_embeeded_to_use_Java},
  { "nativeRandom", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",(void *) gst_randomTextTest},
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
      "org/freedesktop/gstreamer/tutorials/application/StreamSavedVideo");
  //registering methods within
  (*env)->RegisterNatives (env, klass, native_methods,
      G_N_ELEMENTS (native_methods));

  return JNI_VERSION_1_4;
}
