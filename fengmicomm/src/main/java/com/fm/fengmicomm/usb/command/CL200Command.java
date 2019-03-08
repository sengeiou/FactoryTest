package com.fm.fengmicomm.usb.command;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

public class CL200Command {
    private static final String TAG = "CL200Command";
    private static byte[] pc = new byte[]{
            0x02, 0x30, 0x30, 0x35, 0x34, 0x31, 0x20, 0x20, 0x20, 0x03, 0x31, 0x33, 0x0D, 0x0A
    };
    private static byte[] hold = new byte[]{
            0x02, 0x39, 0x39, 0x35, 0x35, 0x31, 0x20, 0x20, 0x30, 0x03, 0x30, 0x32, 0x0D, 0x0A
    };
    private static byte[] ext = new byte[]{
            0x02, 0x30, 0x30, 0x34, 0x30, 0x31, 0x30, 0x20, 0x20, 0x03, 0x30, 0x36, 0x0D, 0x0A
    };
    private static byte[] measure = new byte[]{
            0x02, 0x39, 0x39, 0x34, 0x30, 0x32, 0x31, 0x20, 0x20, 0x03, 0x30, 0x34, 0x0D, 0x0A
    };
    private static byte[] read = new byte[]{
            0x02, 0x30, 0x30, 0x30, 0x32, 0x31, 0x32, 0x30, 0x30, 0x03, 0x30, 0x32, 0x0D, 0x0A
    };
    private String receptor;
    private String command;
    private String param;
    /**
     * 帧的起始位置
     */
    private byte STX = 0x02;
    /**
     * 探测器标识号，默认为字符串"00"
     */
    private byte[] Receptor_Head = new byte[2];
    /**
     * 发送的指令类型,对于读取 照度值 (Ev、x、y)，我们使用字符串"02"
     */
    private byte[] Command = new byte[2];
    /**
     * 参数，字符长度为4，格式"1,CF,0,MODE"
     * CF："2" disable,"3" enable
     * MODE:"0" Normal,"1" multi
     * 单个 CL200设备，参数为:"1200"
     * <p>
     * 对于 CL200 返回的数据，此处为状态位
     * param[0] = "1"~"5" 为正常状态
     * param[1] = " ","4","7"为正常状态
     * param[2] = "1" ~ "4"为正常状态
     * param[3] = "0"为正常状态
     */
    private byte[] Paramter = new byte[4];
    /**
     * 此处为接受到的数据区域，数据范围为 POS(STX)+9 至 POS(ETX)
     * <p>
     * 每次接收到一帧数据后，判断数据帧长度
     * 即检测到 STX 和 DELIMITER 的位置来计算总长度，
     * 再去除 STX、HEAD、Command、Param、ETX、BCC、DELIMITER 的长度
     * 剩下的为 data 长度
     */
    private byte[] data;
    /**
     * EXT 此后再无数据
     */
    private byte ETX = 0x03;
    /**
     * BCC 校验值，位于 EXT 之后
     */
    private byte[] BCC = new byte[2];
    /**
     * 尾巴，结束标志
     */
    private byte[] DELIMITER = new byte[]{0x0D, 0x0A};

    /**
     * 发送帧构造函数
     *
     * @param receptor 感应器编号
     * @param command  命令类型
     * @param param    参数类型
     */
    public CL200Command(String receptor, String command, String param) {
        this.receptor = receptor;
        this.command = command;
        this.param = param;
        init();
    }

    public CL200Command() {
    }

    /**
     * 格式化接受数据帧
     *
     * @param originData 收到的数据帧
     * @return 格式化后的 CL200 数据帧
     */
    public static CL200Command formatReceivedCommand(byte[] originData) {
        int frameStart = 0;
        int frameEnd = 0;
        CL200Command cl200 = new CL200Command();
        //判断帧的起始位置
        for (int i = 0; i < originData.length; i++) {
            if (originData[i] == cl200.STX) {
                frameStart = i;
            }
            if (originData[i] == 0x0D) {
                if ((i + 1) < originData.length && originData[i + 1] == 0x0A) {
                    frameEnd = i + 1;
                }
            }
        }
        int len = frameEnd - frameStart + 1;
        //确认计算的帧长度是否合理
        if (len >= 14) {
            //Receptor_Head 数据获取
            System.arraycopy(originData, frameStart + 1, cl200.Receptor_Head, 0, 2);
            //Command 数据获取
            System.arraycopy(originData, frameStart + 3, cl200.Command, 0, 2);
            //Paramter 数据获取
            System.arraycopy(originData, frameStart + 5, cl200.Paramter, 0, 4);
            //data 数据获取
            if (len - 14 > 0) {
                int datalen = len - 14;
                cl200.data = new byte[datalen];
                System.arraycopy(originData, frameStart + 9, cl200.data, 0, datalen);
            }
            //BCC 数据获取
            int bccPos = len - 4;
            System.arraycopy(originData, frameStart + bccPos, cl200.BCC, 0, 2);
            byte[] calc = cl200.calcBCC();
            if (cl200.BCC[0] == calc[0] && cl200.BCC[1] == calc[1]) {
                return cl200;
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    public static byte[] getDLPCommand(@NonNull String type) {
        byte[] cmd = null;
        switch (type.toUpperCase()) {
            case "PC":
                cmd = pc;
                break;
            case "HOLD":
                cmd = hold;
                break;
            case "EXT":
                cmd = ext;
                break;
            case "MEA":
                cmd = measure;
                break;
            case "READ":
                cmd = read;
                break;
        }
        return cmd;
    }

    @Override
    public String toString() {
        return "CL200Command{" +
                "STX=" + STX +
                ", Receptor_Head=" + Arrays.toString(Receptor_Head) +
                ", Command=" + Arrays.toString(Command) +
                ", Paramter=" + Arrays.toString(Paramter) +
                ", data=" + Arrays.toString(data) +
                ", ETX=" + ETX +
                ", BCC=" + Arrays.toString(BCC) +
                ", DELIMITER=" + Arrays.toString(DELIMITER) +
                '}';
    }

    /**
     * 初始化发送帧
     */
    private void init() {
        if (receptor.length() == 2 && command.length() == 2 && param.length() == 4) {
            Receptor_Head = receptor.getBytes();
            Command = command.getBytes();
            Paramter = param.getBytes();
            BCC = calcBCC();
        } else {
            throw new IllegalArgumentException("IllegalArgumentException receptor:" + receptor + ",command:"
                    + command + ",param:" + param);
        }
    }

    /**
     * BCC 计算
     *
     * @return BCC value
     */
    public byte[] calcBCC() {
        int result = Receptor_Head[0] ^ Receptor_Head[1]
                ^ Command[0] ^ Command[1] ^ Paramter[0]
                ^ Paramter[1] ^ Paramter[2] ^ Paramter[3]
                ^ ETX;
        if (data != null) {
            for (byte datum : data) {
                result ^= datum;
            }
        }
        byte[] res = new byte[2];
        //此处有坑
        //此处为 CL200 校验数据，高4位和低4位分别转字符且大写，取字符的字节数据即可
        String left = Integer.toString(result >> 4, 16).toUpperCase();
        String right = Integer.toString(result & 0b00001111, 16).toUpperCase();
        if (left.length() == 1 && right.length() == 1) {
            res[0] = left.getBytes()[0];
            res[1] = right.getBytes()[0];
        } else {
            Log.d(TAG, "BCC calc error");
        }
        return res;
    }

    /**
     * 针对发送指令的字节转换
     *
     * @return cmd[]
     */
    public byte[] toByteArray() {
        byte[] cmd = new byte[]{
                STX,
                Receptor_Head[0],
                Receptor_Head[1],
                Command[0],
                Command[1],
                Paramter[0],
                Paramter[1],
                Paramter[2],
                Paramter[3],
                ETX,
                BCC[0],
                BCC[1],
                DELIMITER[0],
                DELIMITER[1],
        };

        return cmd;
    }

    public String getData() {
        if (data == null) {
            return "";
        }
        return new String(data);
    }

    /**
     * 根据Parameter，判断当前数据是否有效
     *
     * @return is valid
     */
    public boolean isValid() {
        return Paramter[0] >= 0x31 && Paramter[0] <= 0x35 &&
                (Paramter[1] == 0x20 || Paramter[1] == 0x34 || Paramter[1] == 0x37) &&
                (Paramter[2] >= 0x31 && Paramter[2] <= 0x34) && Paramter[3] == 0x30;
    }

}
