package com.robin.handlerlite;

import android.content.Context;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.robin.handler.HandlerLite;
import com.robin.handler.Looper;
import com.robin.handler.Message;

import org.junit.Test;
import org.junit.runner.RunWith;

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

        assertEquals("com.robin.handlerlite", appContext.getPackageName());


        Looper.prepare();
        final HandlerLite handlerLite=new HandlerLite(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println("当前线程："+Thread.currentThread().getName()+" 消息线程：   "+msg.obj);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = handlerLite.obtainMessage();

                    message.obj = Thread.currentThread().getName();
                    handlerLite.sendMessage(message);
                }

            }
        }).start();

        Looper.loop();
    }
}
