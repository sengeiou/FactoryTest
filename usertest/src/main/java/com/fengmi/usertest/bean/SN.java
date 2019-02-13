package com.fengmi.usertest.bean;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

@XmlBean(name = "SN")
public class SN {
    @XmlAttribute(name = "value")
    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
