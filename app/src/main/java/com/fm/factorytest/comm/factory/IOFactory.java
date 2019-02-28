package com.fm.factorytest.comm.factory;

import com.fm.factorytest.comm.base.CommunicatePort;
import com.fm.factorytest.comm.bean.USBCommunicatePort;
import com.fm.factorytest.comm.vo.USB;

/**
 * 通讯 IO 工厂
 *
 * @author lijie
 * @create 2019-02-22 11:42
 **/
public final class IOFactory {
    private static CommunicatePort port;

    public synchronized static CommunicatePort initPort() {
        if (port == null) {
            port = new USBCommunicatePort();
        }
        return port;
    }

    public synchronized static void resetPort(){
        port.closePort();
        port = null;
    }
}
