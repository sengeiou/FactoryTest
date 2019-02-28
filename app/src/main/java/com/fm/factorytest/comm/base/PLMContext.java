package com.fm.factorytest.comm.base;

import com.fm.factorytest.comm.bean.CommandRxWrapper;
import com.fm.factorytest.comm.server.CommandServer;
import com.fm.factorytest.comm.vo.CommandVO;
import com.fm.factorytest.comm.vo.USB;

import java.util.HashMap;
import java.util.Map;


/**
 * context
 *
 * @author lijie
 * @create 2019-01-08 12:10
 **/
public final class PLMContext {
    public static Map<String, CommandVO> cmdMap = new HashMap<>();
    public static Map<String, CommandRxWrapper> cmdWrapper = new HashMap<>();
    public static CommandServer commandServer = new CommandServer();
    public static volatile USB usb;
    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
