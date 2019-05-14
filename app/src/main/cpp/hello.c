#include <sys/types.h>
#include <jni.h>

JNIEXPORT jstring JNICALL Java_yu_rainash_yix_app_TextNative_getContent(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, "Text From Native");
}