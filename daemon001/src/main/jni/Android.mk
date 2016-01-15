LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := daemon.lock
LOCAL_SRC_FILES := daemon.cpp
LOCAL_LDLIBS := -lm -llog
#include $(BUILD_SHARED_LIBRARY)
include $(BUILD_EXECUTABLE)    # Use this to build an executable.