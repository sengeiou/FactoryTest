package com.fengmi.usertest.bean;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

@XmlBean(name = "MN")
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
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getManu() {
        return manu;
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMouth() {
        return mouth;
    }

    public void setMouth(String mouth) {
        this.mouth = mouth;
    }

    public String getDate_2() {
        return date_2;
    }

    public void setDate_2(String date_2) {
        this.date_2 = date_2;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getRework() {
        return rework;
    }

    public void setRework(String rework) {
        this.rework = rework;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSerial_num_4() {
        return serial_num_4;
    }

    public void setSerial_num_4(String serial_num_4) {
        this.serial_num_4 = serial_num_4;
    }

    public String getFw_version_3() {
        return fw_version_3;
    }

    public void setFw_version_3(String fw_version_3) {
        this.fw_version_3 = fw_version_3;
    }

}
