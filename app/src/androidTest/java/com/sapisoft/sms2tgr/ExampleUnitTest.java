package com.sapisoft.sms2tgr;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /**
     * Instrumentation test, which will execute on an Android device.
     *
     * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
     */
    @RunWith(AndroidJUnit4.class)
    public static class ExampleInstrumentedTest {
        @Test
        public void useAppContext() throws Exception {
            // Context of the app under test.
            Context appContext = InstrumentationRegistry.getTargetContext();

            Assert.assertEquals("com.sapisoft.sms2tg", appContext.getPackageName());
        }
    }
}