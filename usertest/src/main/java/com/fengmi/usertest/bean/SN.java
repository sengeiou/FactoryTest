package com.fengmi.usertest.bean;

import android.text.TextUtils;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

@XmlBean
public class SN {
    @XmlAttribute
    private String value = "00000011";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public synchronized void snIncrement() {
        if (value.length() == 8 && TextUtils.isDigitsOnly(value)) {
            int val = Integer.parseInt(value);
            val++;
            value = Integer.toString(val, 10);
        }
    }
    public String formatSN(){
        if (value.length() == 8){
            return value;
        }
        return null;
    }
}
