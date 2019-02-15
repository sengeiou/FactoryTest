package com.fengmi.usertest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.fengmi.usertest.bean.Config;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import lee.hua.xmlparse.api.XMLAPI;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.fengmi.usertest", appContext.getPackageName());
    }

    @Test
    public void readConfig() throws IOException, ClassNotFoundException {
        XMLAPI.setXmlBeanScanPackage("com.fengmi.usertest.bean");
        // String path = "/storage/F8F0F6A3F0F66772/config/L246-factory.xml";
        //
        // File file = new File(path);
        // if (file.exists()){
        //     Log.e("L246","exist");
        // }else {
        //     Log.e("L246","not found");
        // }
        // FileInputStream ins = new FileInputStream(file);
        // BufferedReader reader = new BufferedReader(new FileReader(file));
        // boolean res;
        // while (res = reader.readLine() != null){
        //     Log.e("L246","" + res);
        // }


        //Log.e("L246","end");
        //Config config = (Config) XMLAPI.readXML();

        //System.out.println(config.getMn());

    }
}
