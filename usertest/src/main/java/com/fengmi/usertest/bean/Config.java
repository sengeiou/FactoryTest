package com.fengmi.usertest.bean;

import lee.hua.xmlparse.annotation.XmlBean;
import lee.hua.xmlparse.annotation.XmlSingleNode;

@XmlBean(name = "Config")
public class Config {
    @XmlSingleNode(name = "manufacture", nodeType = MN.class)
    private MN mn;

    @XmlSingleNode(name = "serial_number", nodeType = SN.class)
    private SN sn;

    public MN getMn() {
        return mn;
    }

    public void setMn(MN mn) {
        this.mn = mn;
    }

    public SN getSn() {
        return sn;
    }

    public void setSn(SN sn) {
        this.sn = sn;
    }
}
