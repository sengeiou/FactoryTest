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
    public static volatile CommandServer commandServer;
    public static volatile USB usb;

}
