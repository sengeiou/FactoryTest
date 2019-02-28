package com.fm.factorytest.comm.bean;


import android.support.annotation.NonNull;

import com.fm.factorytest.comm.base.CommandWrapper;
import com.fm.factorytest.comm.base.GlobalCommandReceiveListener;
import com.fm.factorytest.comm.base.RxDataCallback;
import com.fm.factorytest.comm.vo.CommandVO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-01-08 14:50
 **/
public class CommandRxWrapper extends CommandWrapper {
    private static Map<String, List<RxDataCallback>> listenerMap = new HashMap<>();
    private static GlobalCommandReceiveListener globalReceiveListener = null;
    private byte[] data;
    private boolean receiving = true;


    public CommandRxWrapper() {
        cmdList = new LinkedList<>();
    }

    /**
     * 注册指定 cmd id 的数据接收器
     * @param cmdID 命令 ID
     * @param callback 回调
     */
    public static void addRxDataCallBack(@NonNull String cmdID, @NonNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.add(callback);
        } else {
            callbackList = new LinkedList<>();
            callbackList.add(callback);
            listenerMap.put(cmdID, callbackList);
        }
    }

    /**
     * 移除指定 cmd ID 的监听
     * @param cmdID cmd ID
     * @param callback 回调
     */
    public static void removeRxDataCallBack(@NonNull String cmdID, @NonNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.remove(callback);
        }
    }

    /**
     * 注册全局数据接收，对于所有的数据传输都会调用此接口
     * @param gls 全局监听器
     */
    public static void addGlobalRXListener(@NonNull GlobalCommandReceiveListener gls) {
        globalReceiveListener = gls;
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void received() {
        receiving = false;
        onRxDataRec();
    }

    public String getCmdID() {
        return cmdID;
    }

    public void setCmdID(String cmdID) {
        this.cmdID = cmdID;
    }

    public CommandVO getCmdVO() {
        return cmdVO;
    }

    public void setCmdVO(CommandVO cmdVO) {
        this.cmdVO = cmdVO;
    }

    public void addCommand(Command cmd) {
        cmdList.add(cmd);
    }

    private void onRxDataRec() {
        loadCommandData();
        List<RxDataCallback> callList = listenerMap.get(cmdID);
        if (callList != null) {
            for (RxDataCallback callback : listenerMap.get(cmdID)) {
                callback.notifyDataReceived(cmdID, data);
            }

        }
        if (globalReceiveListener != null) {
            globalReceiveListener.onRXWrapperReceived(cmdID, data);
        }
        cmdList.clear();
    }

    private void loadCommandData() {
        int len = 0;
        Map<Integer, Command> map = new HashMap<>();
        for (Command command : cmdList) {
            len += command.getDataLen();
            map.put((int) command.getCmdNum(), command);
        }

        data = new byte[len];
        byte[] temp;
        int curPos = 0;

        for (int i = 0; i < cmdList.size(); i++) {
            Command cmd = map.get(i);
            if (cmd != null){
                temp = cmd.getData();
                if (temp != null) {
                    System.arraycopy(temp, 0, data, curPos, temp.length);
                    curPos += temp.length;
                }
            }
        }
    }
}
