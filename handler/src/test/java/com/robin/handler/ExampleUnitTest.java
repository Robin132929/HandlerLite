package com.robin.handler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

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