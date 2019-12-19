LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE	:= arrayadder
LOCAL_SRC_FILES	:= arrayadder.c

include $(BUILD_SHARED_LIBRARY)
