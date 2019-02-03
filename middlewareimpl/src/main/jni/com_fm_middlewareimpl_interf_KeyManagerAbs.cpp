#include <jni.h>
#include "com_fm_middlewareimpl_interf_KeyManagerAbs.h"
/* Header for class com_fm_middlewareimpl_interf_KeyManagerAbs */

#ifndef _Included_com_fm_middlewareimpl_interf_KeyManagerAbs
#define _Included_com_fm_middlewareimpl_interf_KeyManagerAbs
#ifdef __cplusplus
extern "C" {
#endif

namespace android {

struct fields_t {
    JavaVM *gJavaVM ;
    JNIEnv* env;
};

static struct fields_t fields;

#define SYST_SERVICES_NAME "system_control"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define UNIFYKEY_NMAE        "hdcp22_fw_private"

#define HDCP_RX_PRIVATE         "hdcp22_rx_private"
#define HDCP_RX                        "hdcp2_rx"
#define HDCP_RX_FW              "extractedKey"

#define IMG_HEAD_SZ                 sizeof(AmlResImgHead_t)
#define ITEM_HEAD_SZ               sizeof(AmlResItemHead_t)
#define ITEM_READ_BUF_SZ (512)

#define INIT_ON             "1"
#define INIT_OFF            "0"
#define EERROR              (char *)"An error has occurred!"
#define RRIGHT              (char *)"That`s OK!"

#define UNIFYKEY_ATTACH     "/sys/class/unifykeys/attach"
#define UNIFYKEY_NAME        "/sys/class/unifykeys/name"
#define UNIFYKEY_WRITE      "/sys/class/unifykeys/write"
#define UNIFYKEY_READ        "/sys/class/unifykeys/read"
#define UNIFYKEY_EXIST       "/sys/class/unifykeys/exist"

#if 1
#define debugP(fmt...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt)
#else
#define debugP(...)
#endif

#define errorP(fmt...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt)

#define WRITE_SIZE     (200*1024)

#define KEY_UNIFY_NAME_LEN	(48)
/* for ioctrl transfer paramters. */
struct key_item_info_t {
	unsigned int id;
	char name[KEY_UNIFY_NAME_LEN];
	unsigned int size;
	unsigned int permit;
	unsigned int flag;		/*bit 0: 1 exsit, 0-none;*/
	unsigned int reserve;
};

#ifndef __HDCP22_HEY_H__
#define __HDCP22_HEY_H__

typedef unsigned int    __u32;
typedef signed int      __s32;
typedef unsigned char   __u8;
typedef signed char     __s8;
typedef __s32 fpi_error;

#define IH_NMLEN		32	/* Image Name Length		*/

#define AML_RES_IMG_V1_MAGIC_LEN    8
#define AML_RES_IMG_V1_MAGIC        "AML_HDK!"//8 chars

#pragma pack(push, 1)
typedef struct pack_header{
	unsigned int	totalSz;/* Item Data total Size*/
	unsigned int	dataSz;	/* Item Data used  Size*/
	unsigned int	dataOffset;	/* Item data offset*/
	unsigned char   type;	/* Image Type, not used yet*/
	unsigned char 	comp;	/* Compression Type	*/
    unsigned short  reserv;
	char 	name[IH_NMLEN];	/* Image Name		*/
}AmlResItemHead_t;
#pragma pack(pop)

//typedef for amlogic resource image
#pragma pack(push, 4)
typedef struct {
    __u32   crc;    //crc32 value for the resouces image
    __s32   version;//0x01 means 'AmlResItemHead_t' attach to each item , 0x02 means all 'AmlResItemHead_t' at the head

    __u8    magic[AML_RES_IMG_V1_MAGIC_LEN];  //resources images magic

    __u32   imgSz;  //total image size in byte
    __u32   imgItemNum;//total item packed in the image

}AmlResImgHead_t;
#pragma pack(pop)
#endif

static sp<ISystemControlService> amSystemControlService;

const sp<ISystemControlService>& getSystemControlService()
{
	if (amSystemControlService.get() == 0) {
		sp<IServiceManager> sm = defaultServiceManager();

		amSystemControlService = interface_cast<ISystemControlService>(sm->getService(String16(SYST_SERVICES_NAME)));
	}
	return amSystemControlService;
}

static unsigned add_sum(const void* pBuf, const unsigned size, unsigned int sum)
{
    const unsigned* data = (const unsigned*)pBuf;
    unsigned wordLen 	 = size>>2;
    unsigned rest 		 = size & 3;

    for (; wordLen/4; wordLen -= 4)
    {
        sum += *data++;
        sum += *data++;
        sum += *data++;
        sum += *data++;
    }
    while (wordLen--)
    {
        sum += *data++;
    }

    if (rest == 0)
    {
        ;
    }
    else if(rest == 1)
    {
        sum += (*data) & 0xff;
    }
    else if(rest == 2)
    {
        sum += (*data) & 0xffff;
    }
    else if(rest == 3)
    {
        sum += (*data) & 0xffffff;
    }

    return sum;
}


//Generate crc32 value with file steam, which from 'offset' to end if checkSz==0
unsigned calc_img_crc(FILE* fp, off_t offset, unsigned checkSz)
{
    unsigned char* buf = NULL;
    unsigned MaxCheckLen = 0;
    unsigned totalLenToCheck = 0;
    const int oneReadSz = 12 * 1024;
    unsigned int crc = 0;

    if (fp == NULL) {
        errorP("bad param!!\n");
        return 0;
    }

    buf = (unsigned char*)malloc(oneReadSz);
    if(!buf){
        errorP("Fail in malloc for sz %d\n", oneReadSz);
        return 0;
    }

    fseeko(fp, 0, SEEK_END);
    MaxCheckLen  = ftell(fp);
    MaxCheckLen -= offset;
    if(!checkSz){
            checkSz = MaxCheckLen;
    }
    else if(checkSz > MaxCheckLen){
            errorP( "checkSz %u > max %u\n", checkSz, MaxCheckLen);
            free(buf);
            return 0;
    }
    fseeko(fp,offset,SEEK_SET);

    while(totalLenToCheck < checkSz)
    {
            int nread;
            unsigned leftLen = checkSz - totalLenToCheck;
            int thisReadSz = leftLen > oneReadSz ? oneReadSz : leftLen;

            nread = fread(buf,1, thisReadSz, fp);
            if (nread < 0) {
                    errorP("%d:read %s.\n", __LINE__, strerror(errno));
                    free(buf);
                    return 0;
            }
            crc = add_sum(buf, thisReadSz, crc);

            totalLenToCheck += thisReadSz;
    }

    free(buf);
    return crc;
}
static char generalDataChange(const char input)
{
	int i;
	char result = 0;

	for(i=0; i<8; i++) {
         if((input & (1<<i)) != 0)
            result |= (1<<(7-i));
         else
            result &= ~(1<<(7-i));
	}

	return result;
}

static void hdcp2DataEncryption(const unsigned len, const char *input, char *out)
{
     int i = 0;

     for(i=0; i<len; i++)
         *out++ = generalDataChange(*input++);
}

static void hdcp2DataDecryption(const unsigned len, const char *input, char *out)
{
     int i = 0;

     for(i=0; i<len; i++)
         *out++ = generalDataChange(*input++);
}

static int write_partiton_raw(const char *partition, const char *data)
{
	String16 writeValue = String16(data);
	if(amSystemControlService->writeSysfs(String16(partition), writeValue)) {
		LOGI("Write OK!");
		return 0;
	} else {
		LOGI("Write failed!");
		return -1;
	}
}

static int write_partiton_keyvalue(const char *partition, const char *data, const int size)
{
	if(amSystemControlService->writeSysfs(String16(partition), data, size)) {
		LOGI("Write OK!");
		return 0;
	} else {
		LOGI("Write failed!");
		return -1;
	}
}

static char *read_partiton_raw(const char *partition)
{
	char *data;
    String16 readValue;
    if(amSystemControlService->readSysfs(String16(partition), readValue)) {
    	data = (char *)String8(readValue).string();

    	LOGI("Read OK!");
    	return data;
    } else {
		LOGI("Read failed!");
		return NULL;
	}
}

static int write_hdcp_key(const char *data, const char *key_name, const int size)
{
    char *status;

    if (write_partiton_raw(UNIFYKEY_ATTACH, "1")) {
        errorP("attach failed!\n");
        return -1;
    }

    if (write_partiton_raw(UNIFYKEY_NAME, key_name)) {
        errorP("name failed!\n");
        return -1;
    }

    if  (write_partiton_keyvalue(UNIFYKEY_WRITE, data, size) == -1) {
        errorP("write failed!\n");
        return -1;
    }

	status = read_partiton_raw(UNIFYKEY_EXIST);

	if (status == NULL)
   	{
        errorP("read status failed!\n");
        return -1;
    }

    if (strcmp(status, "exist"))
	{
        errorP("get status: not burned!\n");
        return -1;
    }

    return 0;
}

void dump_keyitem_info(struct key_item_info_t *info)
{
	if (info == NULL)
		return;
	debugP("id: %d\n", info->id);
	debugP("name: %s\n", info->name);
	debugP("size: %d\n", info->size);
	debugP("permit: 0x%x\n", info->permit);
	debugP("flag: 0x%x\n", info->flag);
	return;
}

void dump_mem(unsigned char * buffer, int count)
{
	int i;

	if (NULL == buffer || count == 0)
	{
		errorP("%s() %d: %p, %d", __func__, __LINE__, buffer, count);
		return;
	}
	for (i=0; i<count ; i+=16)
	{
		if(i % 256 == 0)
			debugP("\n");
		debugP("%02x ", buffer[i]);
	}
	 debugP("\n");
}

static int res_img_unpack(const char *path)
{
    int ret = 0;
    int num = 0;
    int result = -1;
    FILE* fdImg = NULL;
    unsigned int crc32 = 0;
    AmlResImgHead_t *pImgHead = NULL;
    AmlResItemHead_t *pItemHead = NULL;

    if (path == NULL) {
        errorP("Fail path(%s) is null\n", path);
        return -1;
    }

    fdImg = fopen(path, "rb");
    if(!fdImg){
        errorP("Fail to open res image at path %s\n", path);
        return -1;
    }

    void* itemReadBuf = (void*)malloc(ITEM_READ_BUF_SZ);
    if(!itemReadBuf){
        errorP("Fail to malloc buffer at size 0x%x\n", ITEM_READ_BUF_SZ);
        fclose(fdImg);
        return -1;
    }

    int actualReadSz = 0;
    actualReadSz = fread(itemReadBuf, 1, IMG_HEAD_SZ, fdImg);
    if (actualReadSz != IMG_HEAD_SZ){
        errorP("Want to read %d, but only read %d\n", IMG_HEAD_SZ, actualReadSz);
        fclose(fdImg);
        free(itemReadBuf);
        return -1;
    }

    pImgHead = (AmlResImgHead_t *)itemReadBuf;

    if(strncmp(AML_RES_IMG_V1_MAGIC, (char*)pImgHead->magic, AML_RES_IMG_V1_MAGIC_LEN)) {
        errorP("magic error.\n");
        fclose(fdImg);
        free(itemReadBuf);
        return -1;
    }

    crc32 = calc_img_crc(fdImg, 4,  pImgHead->imgSz - 4);
    if(pImgHead->crc != crc32){
        errorP("Error when check crc\n");
        fclose(fdImg);
        free(itemReadBuf);
        return -1;
    }

    fseek(fdImg, IMG_HEAD_SZ, SEEK_SET);
    int ItemHeadSz = (pImgHead->imgItemNum)*ITEM_HEAD_SZ;
    actualReadSz = fread(itemReadBuf+IMG_HEAD_SZ, 1, ItemHeadSz, fdImg);
    if (actualReadSz != ItemHeadSz){
        errorP("Want to read 0x%x, but only read 0x%x\n", ItemHeadSz, actualReadSz);
        fclose(fdImg);
        free(itemReadBuf);
        return -1;
    }

    pItemHead = (AmlResstatic char generalDataChange(const char input)
{
	int i;
	char result = 0;

	for(i=0; i<8; i++) {
         if((input & (1<<i)) != 0)
            result |= (1<<(7-i));
         else
            result &= ~(1<<(7-i));
	}

	return result;
}

static void hdcp2DataEncryption(const unsigned len, const char *input, char *out)
{
     int i = 0;

     for(i=0; i<len; i++)
         *out++ = generalDataChange(*input++);
}

static void hdcp2DataDecryption(const unsigned len, const char *input, char *out)
{
     int i = 0;

     for(i=0; i<len; i++)
         *out++ = generalDataChange(*input++);
}

static int write_partiton_raw(const char *partition, const char *data)
{
	String16 writeValue = String16(data);
	if(amSystemControlService->writeSysfs(String16(partition), writeValue)) {
		LOGI("Write OK!");
		return 0;
	} else {
		LOGI("Write failed!");
		return -1;
	}
}

static int write_partiton_keyvalue(const char *partition, const char *data, const int size)
{
	if(amSystemControlService->writeSysfs(String16(partition), data, size)) {
		LOGI("Write OK!");
		return 0;
	} else {
		LOGI("Write failed!");
		return -1;
	}
}

static char *read_partiton_raw(const char *partition)
{
	char *data;
    String16 readValue;
    if(amSystemControlService->readSysfs(String16(partition), readValue)) {
    	data = (char *)String8(readValue).string();

    	LOGI("Read OK!");
    	return data;
    } else {
		LOGI("Read failed!");
		return NULL;
	}
}

static int write_hdcp_key(const char *data, const char *key_name, const int size)
{
    char *status;

    if (write_partiton_raw(UNIFYKEY_ATTACH, "1")) {
        errorP("attach failed!\n");
        return -1;
    }

    if (write_partiton_raw(UNIFYKEY_NAME, key_name)) {
        errorP("name failed!\n");
        return -1;
    }

    if  (write_partiton_keyvalue(UNIFYKEY_WRITE, data, size) == -1) {
        errorP("write failed!\n");
        return -1;
    }

	status = read_partiton_raw(UNIFYKEY_EXIST);

	if (status == NULL)
   	{
        errorP("read status failed!\n");
        return -1;
    }

    if (strcmp(status, "exist"))
	{
        errorP("get status: not burned!\n");
        return -1;
    }

    return 0;
}

void dump_keyitem_info(struct key_item_info_t *info)
{
	if (info == NULL)
		return;
	debugP("id: %d\n", info->id);
	debugP("name: %s\n", info->name);
	debugP("size: %d\n", info->size);
	debugP("permit: 0x%x\n", info->permit);
	debugP("flag: 0x%x\n", info->flag);
	return;
}

void dump_mem(unsigned char * buffer, int count)
{
	int i;

	if (NULL == buffer || count == 0)
	{
		errorP("%s() %d: %p, %d", __func__, __LINE__, buffer, count);
		return;
	}
	for (i=0; i<count ; i+=16)
	{
		if(i % 256 == 0)
			debugP("\n");
		debugP("%02x ", buffer[i]);
	}
	 debugP("\n");
}
ItemHead_t *)(itemReadBuf+IMG_HEAD_SZ);
    for (num=0; num < pImgHead->imgItemNum; num++, pItemHead++)
    {
        debugP("pItemHead->name:%s\n", pItemHead->name);
        debugP("pItemHead->size:%d\n", pItemHead->dataSz);
        debugP("pItemHead->dataOffset:%d\n", pItemHead->dataOffset);

        if (!strcmp(pItemHead->name, HDCP_RX_PRIVATE)){
            void *tmpbuffer = (void *)malloc(pItemHead->dataSz+4);
            if (!tmpbuffer) {
                errorP("Fail to malloc buffer  size 0x%x\n", pItemHead->dataSz+4);
                fclose(fdImg);
                free(itemReadBuf);
                return -1;
            }
            char *writebuffer = (char *)malloc(pItemHead->dataSz+4);
            if (!writebuffer) {
                errorP("Fail to malloc buffer  size 0x%x\n", pItemHead->dataSz+4);
                fclose(fdImg);
                free(itemReadBuf);
                free(tmpbuffer);
                return -1;
            }

            memset(tmpbuffer, 0, pItemHead->dataSz+4);
            memset(writebuffer, 0, pItemHead->dataSz+4);
            fseek(fdImg, pItemHead->dataOffset, SEEK_SET);
            int readlen = fread(tmpbuffer, 1, pItemHead->dataSz, fdImg);
            if (readlen != pItemHead->dataSz) {
                fclose(fdImg);
                free(itemReadBuf);
                free(tmpbuffer);
                free(writebuffer);
                return -1;
            }

            for (int i=0; i< pItemHead->dataSz; i++) {
                debugP("tmpbuffer[%d]:%x\n", i,((unsigned char *)tmpbuffer)[i]);
            }

            hdcp2DataDecryption(pItemHead->dataSz, (char *)tmpbuffer, (char *)writebuffer);
            free(tmpbuffer);

            for (int i=0; i< pItemHead->dataSz; i++) {
                debugP("writebuffer[%d]:%x\n", i,((unsigned char *)writebuffer)[i]);
            }

            result = write_hdcp_key(writebuffer, HDCP_RX_PRIVATE, pItemHead->dataSz);
            free(writebuffer);
            if (result) {
                errorP("write hdcp key failed1!\n");
                free(itemReadBuf);
                fclose(fdImg);
                return -1;
            }
			else
			{
				errorP("write hdcp key OK1!\n");
			}
        }else if (!strcmp(pItemHead->name, HDCP_RX)) {
        	#if 1
            char *writebuffer = (char *)malloc(pItemHead->dataSz+4);
            if (!writebuffer) {
                errorP("Fail to malloc buffer  size 0x%x\n", pItemHead->dataSz+4);
                fclose(fdImg);
                free(itemReadBuf);
                return -1;
            }

            memset(writebuffer, 0, pItemHead->dataSz+4);
            fseek(fdImg, pItemHead->dataOffset, SEEK_SET);
            int readlen = fread(writebuffer, 1, pItemHead->dataSz, fdImg);
            if (readlen != pItemHead->dataSz) {
                fclose(fdImg);
                free(itemReadBuf);
                free(writebuffer);
                return -1;
            }

            result = write_hdcp_key(writebuffer, HDCP_RX, pItemHead->dataSz);
            free(writebuffer);
            if (result) {
                errorP("write hdcp key failed2!\n");
                free(itemReadBuf);
                fclose(fdImg);
                return -1;
            }
			#endif
        } else if (!strcmp(pItemHead->name, HDCP_RX_FW)) {
			#if 1
			char *writebuffer = (char *)malloc(pItemHead->dataSz+4);
            if (!writebuffer) {
                errorP("Fail to malloc buffer  size 0x%x\n", pItemHead->dataSz+4);
                fclose(fdImg);
                free(itemReadBuf);
                return -1;
            }

            memset(writebuffer, 0, pItemHead->dataSz+4);
            fseek(fdImg, pItemHead->dataOffset, SEEK_SET);
            int readlen = fread(writebuffer, 1, pItemHead->dataSz, fdImg);
            if (readlen != pItemHead->dataSz) {
                fclose(fdImg);
                free(itemReadBuf);
                free(writebuffer);
                return -1;
            }

            result = write_hdcp_key(writebuffer, "hdcp22_rx_fw", pItemHead->dataSz);

            free(writebuffer);
            if (result) {
                errorP("write hdcp key failed3!\n");
                free(itemReadBuf);
                fclose(fdImg);
                return -1;
            }
			#endif
        }
    }

    fclose(fdImg);
    free(itemReadBuf);
    return result;
}

/*
 * Class:     com_fm_middlewareimpl_interf_KeyManagerAbs
 * Method:    setImgPath
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fm_middlewareimpl_interf_KeyManagerAbs_setImgPath
  (JNIEnv *env, jobject obj, jstring path)
  {
	char *messageReturn;
    char *imgPath = (char *)env->GetStringUTFChars(path, NULL);

	LOGI("imgPath: %s\n", imgPath);

	int ret = res_img_unpack(imgPath);
    if (ret == 0){
	    messageReturn = "That`s OK!";
    } else if (ret == -1) {
    	messageReturn = "An error has occurred!";
    }

    env->ReleaseStringUTFChars(path, imgPath);
    return env->NewStringUTF(messageReturn);
  }


static bool aml_key_set_name(const char *name)
{
	LOGI("aml_key_set_name!");

	String16 writeValue = String16(name);
	bool ret = amSystemControlService->writeSysfs(String16(UNIFYKEY_NAME), writeValue);
    if (ret) {
		LOGI("aml_key_set_name_ok!");
    } else {
    	LOGI("aml_key_set_name_failed!");
    }
	return ret;
}

static bool aml_key_set_value(const char *value, const int size)
{
	LOGI("aml_key_set_value!");
	bool ret = amSystemControlService->writeSysfs(String16(UNIFYKEY_WRITE), value, size);
    if (ret) {
		LOGI("aml_key_set_value_ok!");
    } else {
    	LOGI("aml_key_set_value_failed!");
    }
	return ret;
}

/*
 * Class:     com_fm_middlewareimpl_interf_KeyManagerAbs
 * Method:    aml_key_write
 * Signature: (Ljava/lang/String;[B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fm_middlewareimpl_interf_KeyManagerAbs_aml_1key_1write
  (JNIEnv *env, jobject obj, jstring keyName, jbyteArray keyValue)
  {

	LOGI("aml_key_write!");

	char *messageReturn;
	char *KeyName = (char *)env->GetStringUTFChars(keyName, NULL);

	char *keyValueChar = NULL;
	jsize keyValueLen = env->GetArrayLength(keyValue); //»ñÈ¡³¤¶È
	LOGI("keyValueLen = %d\n", keyValueLen);
	jbyte *keyValueByte = env->GetByteArrayElements(keyValue, 0); //jbyteArray×ªÎªjbyte*

    if (keyValueLen > 0) {
		keyValueChar = (char *)malloc(keyValueLen);
		if (keyValueChar) {
			memset(keyValueChar, 0, keyValueLen);
			memcpy(keyValueChar, keyValueByte, keyValueLen);
		}
    }
	env->ReleaseByteArrayElements(keyValue, keyValueByte, 0);
	LOGI("keyValueChar Length = %d\n", strlen(keyValueChar));
	LOGI("keyValueChar = %s\n", keyValueChar);
	if (aml_key_set_name(KeyName)) {
		if (aml_key_set_value(keyValueChar, keyValueLen)) {
    		messageReturn = RRIGHT;
		} else {
			messageReturn = EERROR;
		}
	}

	//ÊÍ·Å·ÖÅäµÄÄÚ´æ£¬·ÀÖ¹Ò°Ö¸Õë
	if (keyValueChar) {
		free(keyValueChar);
		keyValueChar = NULL;
	}
	return env->NewStringUTF(messageReturn);
  }
/*
 * Class:     com_fm_middlewareimpl_interf_KeyManagerAbs
 * Method:    aml_key_read
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fm_middlewareimpl_interf_KeyManagerAbs_aml_1key_1read
(JNIEnv *env, jobject obj, jstring keyName)
{
	LOGI("aml_key_read!");

	char *messageReturn;
	char *KeyName = (char *)env->GetStringUTFChars(keyName, NULL);

	if (aml_key_set_name(KeyName)) {
		messageReturn = aml_key_get_value();
	}

	return env->NewStringUTF(messageReturn);
}

/*
 * Class:     com_fm_middlewareimpl_interf_KeyManagerAbs
 * Method:    aml_key_get_name
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fm_middlewareimpl_interf_KeyManagerAbs_aml_1key_1get_1name
  (JNIEnv *env, jobject obj)
  {
  	LOGI("aml_key_get_name!");

  	String16 readValue;
  	char *returnValue;
  	bool ret = amSystemControlService->readSysfs(String16(UNIFYKEY_NAME), readValue);
  	returnValue = (char *)String8(readValue).string();
      if (ret) {
  		LOGI("aml_key_get_name_ok!");
      	return env->NewStringUTF(returnValue);;
      } else {
      	LOGI("aml_key_get_name_failed!");
      	return env->NewStringUTF(EERROR);
      }
  }

#ifdef __cplusplus
}
#endif
#endif
