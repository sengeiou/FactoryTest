package com.fengmi.usertest.bean;

import android.text.TextUtils;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

@XmlBean(name = "manufacture")
public class MN {
    @XmlAttribute(name = "product")
    private String product = "";

    @XmlAttribute(name = "manufacture")
    private String manu = "";

    @XmlAttribute
    private String type = "";

    @XmlAttribute
    private String year = "";

    @XmlAttribute
    private String mouth = "";

    @XmlAttribute(name = "date")
    private String date_2 = "";

    @XmlAttribute
    private String space = "";

    @XmlAttribute
    private String rework = "";

    @XmlAttribute
    private String color = "";

    @XmlAttribute(name = "serial_num")
    private String serial_num_4 = "";

    @XmlAttribute(name = "fw_version")
    private String fw_version_3 = "";

    public String getProduct() {
        return strComplete(product,1);
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getManu() {
        return strComplete(mouth,1);
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public String getType() {
        return strComplete(type,1);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return strComplete(year,1);
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMouth() {
        return strComplete(mouth,1);
    }

    public void setMouth(String mouth) {
        if (TextUtils.isDigitsOnly(mouth)) {
            int m = Integer.parseInt(mouth);
            this.mouth = Integer.toHexString(m).toUpperCase();
        }
        this.mouth = mouth;
    }

    public String getDate_2() {
        return strComplete(date_2,2);
    }

    public void setDate_2(String date_2) {
        if (date_2.length() < 2) {
            date_2 = "0" + date_2;
        }
        this.date_2 = date_2;
    }

    public String getSpace() {
        return strComplete(space,1);
    }

    public void setSpace(String space) {
        if (space.length() < 1) {
            space = "0";
        }
        this.space = space.toUpperCase();
    }

    public String getRework() {
        return strComplete(rework,1);
    }

    public void setRework(String rework) {
        this.rework = rework;
    }

    public String getColor() {
        return strComplete(color,1);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSerial_num_4() {
        return strComplete(serial_num_4,4);
    }

    public void setSerial_num_4(String serial_num_4) {
        if (serial_num_4.length() < 4) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4 - serial_num_4.length(); i++) {
                sb.append("0");
            }
            serial_num_4 = sb.toString() + serial_num_4;
        }
        this.serial_num_4 = serial_num_4;
    }

    public String getFw_version_3() {
        return strComplete(fw_version_3,3);
    }

    public void setFw_version_3(String fw_version_3) {
        if (fw_version_3.length() < 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3 - fw_version_3.length(); i++) {
                sb.append("0");
            }
            fw_version_3 = sb.toString() + fw_version_3;
        }
        this.fw_version_3 = fw_version_3;
    }

    public String formatMN() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProduct())
                .append(getManu())
                .append(getType())
                .append(getYear())
                .append(getMouth());
        sb.append(getDate_2())
                .append(getSpace())
                .append(getRework())
                .append(getColor());
        sb.append(getSerial_num_4());
        sb.append(getFw_version_3());

        String mn = sb.toString();
        System.out.println(mn);
        if (mn.length() != 17) {
            System.out.println("MN len error");
            return null;
        } else {
            return mn;
        }
    }

    public synchronized void snIncrement(){
        if (serial_num_4.length() <= 4 && TextUtils.isDigitsOnly(serial_num_4)){
            int val = Integer.parseInt(serial_num_4);
            val++;
            serial_num_4 = Integer.toString(val,10);
            setSerial_num_4(serial_num_4);
        }
    }

    @Override
    public String toString() {
        return formatMN();
    }

    private String strComplete(String str,int len){
        int strLen = str.length();
        if (strLen <= len){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len - strLen; i++) {
                sb.append("0");
            }
            sb.append(str);
            return sb.toString().toUpperCase();
        }else {
            return str.substring(0,len).toUpperCase();
        }
    }
}
