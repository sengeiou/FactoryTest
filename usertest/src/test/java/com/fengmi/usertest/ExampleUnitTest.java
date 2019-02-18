package com.fengmi.usertest;

import com.fengmi.usertest.bean.Config;
import com.fengmi.usertest.bean.MN;
import com.fengmi.usertest.bean.SN;

import org.junit.Test;

import java.io.IOException;

import lee.hua.xmlparse.api.XMLAPI;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void generate(){
        Config config = new Config();
        config.setMn(new MN());
        config.setSn(new SN());

        Util.writeConfig(config,"/Users/lijie/Desktop/L246.xml");
    }

    @Test
    public void read() throws IOException, ClassNotFoundException {
        String str = "MTTT";
        str = str.substring(0,3);
        System.out.println(str.matches("[a-zA-Z]{3}"));

        // XMLAPI.setXmlBeanScanPackage("com.fengmi.usertest.bean");
        // Config conf = Util.readConfig("/Users/lijie/Desktop/L246-factory.xml");
        //
        // System.out.println(conf.getMn().toString());
        // conf.getMn().snIncrement();
        //System.out.println(conf.getMn().getFw_version_3());
        //SN sn = conf.getSn();

        //sn.snIncrement();

        //Util.writeConfig(conf,"/Users/lijie/Desktop/L246.xml");
    }
}