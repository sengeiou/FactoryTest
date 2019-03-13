package com.fm.fengmicomm;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.fm.fengmicomm.usb.command.CL200Command;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

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

        assertEquals("com.fm.fengmicomm.test", appContext.getPackageName());
    }

    @Test
    public void testCL200() {
        byte[] errorCRC = new byte[]{
                2, 48, 48, 48, 50, 49, 32, 50, 48, 43, 52, 50, 52, 51, 51, 43, 51, 50, 54, 56, 48, 43, 51, 53, 57, 52, 48, 3, 48, 70, 13, 10};
        byte[] okCRC = new byte[]{
                2, 48, 48, 48, 50, 49, 32, 50, 48, 43, 54, 56, 52, 52, 51, 43, 51, 50, 53, 52, 48, 43, 51, 53, 52, 53, 48, 3, 48, 51, 13, 10};

        int ec = 0;
        int oc = 0;
        for (int i = 1; i < 28; i++) {
            ec ^= errorCRC[i];
            oc ^= okCRC[i];
        }
        byte[] res = new byte[2];
        String left = Integer.toString(ec >> 4,16);
        String right = Integer.toString(ec & 0b00001111,16);
        if (left.length() == 1 && right.length() == 1) {
            res[0] = left.getBytes()[0];
            res[1] = right.getBytes()[0];
        }
        left = Integer.toString(oc >> 4);
        right = Integer.toString(oc & 0b00001111);

    }
}
