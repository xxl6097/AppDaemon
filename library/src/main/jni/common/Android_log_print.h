/**
 * Created by Android Studio.
 * Author: UUXIA
 * Date: 2015-11-12 17:02
 * Description:  Android JNI Log打印宏定义文件
 */


#ifndef BIND_ANDROID_LOG_PRINT_H
#define BIND_ANDROID_LOG_PRINT_H
#include <android/log.h>

#define IS_DEBUG

#ifdef IS_DEBUG

#define LOG_TAG ("uulog.jni")

#define LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG  , LOG_TAG, __VA_ARGS__))

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO   , LOG_TAG, __VA_ARGS__))

#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN   , LOG_TAG, __VA_ARGS__))

#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__))

#else

#define LOGV(LOG_TAG, ...) NULL

#define LOGD(LOG_TAG, ...) NULL

#define LOGI(LOG_TAG, ...) NULL

#define LOGW(LOG_TAG, ...) NULL

#define LOGE(LOG_TAG, ...) NULL

#endif
#ifdef IS_DEBUG
#define Logc(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#else
#define Logc(format,...) printf(format,##__VA_ARGS__)
#endif // LOGI

#endif //BIND_ANDROID_LOG_PRINT_H
