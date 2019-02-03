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
 * 主要定义了外围存储设备的的测试接口，比如：USB-Host，TF card等。
 * 这里为了接口的继承性，我们将定义新老两种接口。
 * 本接口定义了如下信息的存取：
 * old API
 * 1. USB3.0测试，U盘插入后会自动挂载，因此可以直接开始测试。（usbHost30Test)
 * 2. USB2.0测试，大多数时候，我们使用这个接口控制DUT，不需要测试，但是留下这个接口。（usbHost20Test)
 * 3. TF卡测试，类似U盘，插入后自动挂载，可以直接开始测试。（tfCardTest)
 * 4. USB3.0卸载，为了安全使用U盘，需要主动卸载。（usbHost30Unmount)
 * 5. USB2.0卸载，为了安全使用U盘，需要主动卸载。（usbHost20Unmount)
 * 6. TFcard卸载，为了安全使用TF，需要主动卸载。（tfCardUnmount)
 * new API: 暂时用不到，他们的效率并不高，所以先保留吧。
 * 1. 外部存储设备测试接口。（externalMemoryTest)
 * 2. 卸载所有外部设备。（externalMemoryUnmount)
 * note: 目前来说，我们仅支持1个sd卡和2个usb （其中1个用于测试通信；另一个作为被测接口。
 * 如果再多的话，命名规则要重新定义。
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;



public abstract class StorageManagerAbs extends BaseMiddleware {
    public StorageManagerAbs(Context context) {
        super(context);
    }

    /**
     * Usb Host 3.0 test: read U disk predefined file and it matchs with local file.
     * note: 见本file的note
     *
     * @return success or no.
     */
    public abstract boolean usbHost30Test();

    /**
     * Usb Host 2.0 test: read U disk predefined file and it matchs with local file.
     * note: 见本file的note
     *
     * @return success or no.
     */
    public abstract boolean usbHost20Test();

    /**
     * TF card test: read TF disk predefined file and it matchs with local file.
     * note: 见本file的note
     *
     * @return success or no.
     */
    public abstract boolean tfCardTest();

    /**
     * U disk 3.0 Unmount: Unmount U disk to protect it.
     *
     * @return success or no.
     */
    public abstract boolean usbHost30Unmount();

    /**
     * U disk 2.0 Unmount: Unmount U disk to protect it.
     *
     * @return success or no.
     */
    public abstract boolean usbHost20Unmount();

    /**
     * TF card Unmount: Unmount TF disk to protect it.
     *
     * @return success or no.
     */
    public abstract boolean tfCardUnmount();
    /*------------------------------- new Interface ---------------------*/

    /**
     * principle: by storageManager, you can enum all external disk and test them
     * one by one.
     * note: in different disk, the target file is different. for example, in first USB2.0,
     * the file content is "First"; in second USB3.0, the file content is "Second"; in third TF card,
     * the file content is "Third". anyway, the file name is same.
     *
     * @return the type is integer:
     * 0: pass
     * 1: first disk failed
     * 2: second disk failed
     * 3: third disk failed
     */
    public abstract int externalMemoryTest();

    /**
     * Unmount: unmount all of external disk;
     *
     * @return the type is pass/fail
     */
    public abstract boolean externalMemoryUnmount();

    /**
     * change adb to usb;
     *
     * @return the type is pass/fail
     */
    public abstract boolean adbToUsb();

    /**
     * check the aimed usb disk speed;
     *
     * @return the speed value by String
     */
    public abstract String usbSpeedCheck(String id);

    /**
     * test the usb disk content and speed;
     *
     * @return the speed value by String
     */
    public abstract boolean usbContent2SpeedTest(String Name, String UsbType);

    public abstract boolean usbFileCheck(String name);
}
