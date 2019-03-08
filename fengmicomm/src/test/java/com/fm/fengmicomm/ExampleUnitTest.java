package com.fm.fengmicomm;

import com.fm.fengmicomm.usb.command.CL200Command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private CL200Command pc = new CL200Command("00", "54", "1   ");
    private CL200Command hold = new CL200Command("99", "55", "1  0");
    private CL200Command ext = new CL200Command("00", "40", "10  ");
    private CL200Command measure = new CL200Command("99", "40", "21  ");
    private CL200Command read = new CL200Command("00", "02", "1200");

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testBCC() {
        for (byte aByte : pc.toByteArray()) {
            System.out.println(Integer.toHexString(aByte));
        }
        System.out.println("==============================");
        for (byte b : hold.toByteArray()) {
            System.out.println(Integer.toHexString(b));
        }
        System.out.println("==============================");
        for (byte b : ext.toByteArray()) {
            System.out.println(Integer.toHexString(b));
        }
        System.out.println("==============================");
        for (byte b : measure.toByteArray()) {
            System.out.println(Integer.toHexString(b));
        }
        System.out.println("==============================");
        for (byte b : read.toByteArray()) {
            System.out.println(Integer.toHexString(b));
        }

    }

}