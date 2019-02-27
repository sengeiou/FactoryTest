package com.fm.factorytest.comm.bean;


import android.support.annotation.NonNull;

import com.fm.factorytest.comm.base.CommandWrapper;
import com.fm.factorytest.comm.base.RxDataCallback;
import com.fm.factorytest.comm.vo.CommandVO;

import java.util.*;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-01-08 14:50
 **/
public class CommandRxWrapper extends CommandWrapper {
    private byte[] data;
    private boolean receiving = true;

    private static Map<String, List<RxDataCallback>> listenerMap = new HashMap<>();


    public CommandRxWrapper() {
        cmdList = new LinkedList<>();
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void received() {
        receiving = false;
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

    public static void addRxDataCallBack(@NonNull String cmdID, @NonNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null){
            callbackList.add(callback);
        }else {
            callbackList = new LinkedList<>();
            callbackList.add(callback);
            listenerMap.put(cmdID,callbackList);
        }
    }

    public static void removeRxDataCallBack(@NonNull String cmdID, @NonNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.remove(callback);
        }
    }

    public void onRxDataRec() {
        loadCommandData();
        List<RxDataCallback> callList = listenerMap.get(cmdID);
        if (callList != null) {
            for (RxDataCallback callback : listenerMap.get(cmdID)) {
                callback.notifyDataReceived(cmdID, data);
            }

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
            temp = map.get(i).getData();
            if (temp != null) {
                System.arraycopy(temp, 0, data, curPos, temp.length);
                curPos += temp.length;
            }
        }
    }
}
