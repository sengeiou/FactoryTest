/*
 * Copyright (C) 2013 XiaoMi Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 本部分实现了工厂本地的一些全局变量，全局属性的操作，主要有如下接口：
 * 1. 初始化本地全局属性（initLocalProperty）
 * 2. 清楚全部属性（clearLocalProperty）
 * 3. 获取属性文件的保存路径（getLocalPropertyPath）
 * 4. 读写字符格式的属性值（setLocalPropString, getLocalPropString）
 * 4. 读写整型格式的属性值（setLocalPropInt, getLocalPropInt）
 * 5. 读写布尔格式的属性值（setLocalPropBool, getLocalPropBool）
 * 6. 对整形格式的属性值按照指定step进行增加（increaseLocalPropInt）
 * 7. 写入一些产品属性（writeProductFeatures）
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;



public abstract class LocalPropertyManagerAbs extends BaseMiddleware {
    public LocalPropertyManagerAbs(Context context) {
        super(context);
    }

    /**
     * init Local property file. if file exists, cleared; if not, created.
     *
     * @return success or no.
     */
    public abstract boolean initLocalProperty();

    /**
     * rebuild this file (delete it and recreate it)
     *
     * @return success or no.
     */
    public abstract boolean clearLocalProperty();

    /**
     * fetch the path that saving property file
     *
     * @return String
     */
    public abstract String getLocalPropertyPath();

    /**
     * set the propertyś KeyValuePair, the value type is string.
     *
     * @param key: the property name; value, the key´s value (length is less than 128)
     * @return failure or success (false or true)
     */
    public abstract boolean setLocalPropString(String key, String value);

    /**
     * return the value corresponding with key
     *
     * @param key: the property name
     * @return String
     */
    public abstract String getLocalPropString(String key);

    /**
     * set the propertyś KeyValuePair, the value type is int.
     *
     * @param key: the property name; value, the key´s value which is 32 bits.
     * @return failure or success (false or true)
     */
    public abstract boolean setLocalPropInt(String key, int value);

    /**
     * return the value corresponding with key
     *
     * @param key: the property name
     * @return int
     */
    public abstract int getLocalPropInt(String key);

    /**
     * set the propertyś KeyValuePair, the value type is boolean.
     *
     * @param key: the property name; value, the key´s value
     * @return failure or success (false or true)
     */
    public abstract boolean setLocalPropBool(String key, boolean value);

    /**
     * return the value corresponding with key
     *
     * @param key: the property name
     * @return boolean
     */
    public abstract boolean getLocalPropBool(String key);

    /**
     * set the keyś Value increase with the given increment (value)
     *
     * @param key: the property name; value, the increment based on current key´s value.
     * @return failure or success (false or true)
     */
    public abstract boolean increaseLocalPropInt(String key, int value);


    /**
     * set the product features
     *
     * @return failure or success (false or true)
     */
    public abstract String getAppPropertyPath();

    /**
     * set the app property for string
     *
     * @param key, key value
     * @return failure or success
     */
    public abstract boolean setAppPropString(String key, String value);

    /**
     * get the app property for String
     *
     * @param key: key
     * @return string (key value)
     */
    public abstract String getAppPropString(String key);

    /**
     * set the app property for int
     *
     * @param key, key content
     * @return failure or success
     */
    public abstract boolean setAppPropInt(String key, int value);

    /**
     * get the app property for int
     *
     * @param key content
     * @return int (key value)
     */
    public abstract int getAppPropInt(String key);

    /**
     * set the app property for bool
     *
     * @param key , key content
     * @return failure or success
     */
    public abstract boolean setAppPropBool(String key, boolean value);

    /**
     * get the app property for bool
     *
     * @param  key
     * @return key vlaue (false or true)
     */
    public abstract boolean getAppPropBool(String key);
}
