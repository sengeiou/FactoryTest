LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := hdcp_jni
LOCAL_SRC_FILES := com_fm_middlewareimpl_interf_KeyManagerAbs.cpp
include $(BUILD_SHARED_LIBRARY)
