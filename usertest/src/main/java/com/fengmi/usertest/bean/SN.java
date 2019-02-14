package com.fengmi.usertest.bean;

import android.text.TextUtils;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

@XmlBean(name = "serial_number")
public class SN {
    @XmlAttribute(name = "value")
    private String value = "";

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
}
