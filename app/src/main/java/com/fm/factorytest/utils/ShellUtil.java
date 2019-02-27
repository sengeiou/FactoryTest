package com.fm.factorytest.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ShellUtil {
    private static final String TAG = "ShellUtil";

    public static int formatDelayNumber(String execCommand) throws NumberFormatException {
        String s1 = execCommand.replace("\n", "");
        String s2 = s1.trim();
        return Integer.valueOf(s2);
    }

    public static StringBuilder callCommand(String command) throws IOException {
        //Log.i(TAG, "execCommand: " + command);
        // start the ls command running
        // String[] args = new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command); // 这句话就是shell与高级语言间的调用
        // 如果有参数的话可以用另外一个被重载的exec方法
        // 实际上这样执行时启动了一个子进程,它没有父进程的控制台
        // 也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        // read the ls output
        String line = "";
        StringBuilder sb = new StringBuilder(line);
        while ((line = bufferedreader.readLine()) != null) {
            // System.out.println(line);
            sb.append(line);
            sb.append('\n');
        }
        // tv.setText(sb.toString());
        // 使用exec执行不会等执行成功以后才返回,它会立即返回
        // 所以在某些情况下是很要命的(比如复制文件的时候)
        // 使用wairFor()可以等待命令执行完成以后才返回
        try {
            if (proc.waitFor() != 0) {
                Log.e(TAG, "exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sb;
    }

    public static String execCommand(String command) throws IOException {
        StringBuilder sb = callCommand(command);
        return sb.toString();
    }

    public static boolean isI2CReady() throws IOException {
        String status = callCommand("cat /sys/class/projector/laser-projector/i2c_busy").toString();
        if (status != null && status.length() >= 1) {
            if (status.startsWith("-")) {
                status = status.substring(0, 2);
            } else {
                status = status.substring(0, 1);
            }
        }
        boolean result = false;
        switch (status) {
            case "1":
                Log.e(TAG, "i2c status is 1, not ready");
                break;
            case "0":
                result = true;
                Log.e(TAG, "i2c status is 0, ready");
                break;
            case "-1":
                Log.e(TAG, "i2c status is -1, not batman no permit");
                result = true;
                break;
            default:
                Log.e(TAG, "i2c status is " + status + ", unknow status");
                break;
        }
        return result;
    }
}
