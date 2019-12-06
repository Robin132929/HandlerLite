package com.robin.handler;

import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Modifier;

public class HandlerLite {
    private static final String TAG = "HandlerLite";
    Runnable callback;
    private Looper myLooper;
    private MessageQueue mQueue;
    private Callback mCallback;

    /**
     * 构造函数
     */
    public HandlerLite() {
        this(null);
    }

    /**
     * @param callback
     */
    public HandlerLite(Callback callback) {
        //获取looper实例
        myLooper = Looper.myLooper();
        if (myLooper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = myLooper.mQueue;
        mCallback = callback;
    }

    /**
     * 从message缓存池中获取一个message 推荐用此方法获取message实例或者直接调用Message.obtain()
     *
     * @return
     */
    public Message obtainMessage() {
        return Message.obtain();
    }

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    private boolean sendMessageDelayed(Message msg, long delay) {
        if (delay < 0) {
            delay = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delay);
    }

    public final boolean sendMessageAtTime(Message msg, long delay) {
        MessageQueue messageQueue = mQueue;
        if (messageQueue == null) {
            RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;

        }
        return enQueueMessage(messageQueue, msg, delay);
    }

    private boolean enQueueMessage(MessageQueue messageQueue, Message msg, long delay) {
        msg.target = this;
        return messageQueue.enQueueMessage(msg, delay);
    }

    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

    /**
     * handler 必须重写的方法
     *
     * @param msg
     */
    public void handleMessage(Message msg) {

    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }

    /**
     * @param message
     * @return mesage name if callback is not null return the class name of the
     * message callback else return he hexadecimal representation of the
     * message "what" field.
     */
    public String getMessageName(Message message) {
        if (message.callback != null) {
            return message.callback.getClass().getName();
        }
        return "0x" + Integer.toHexString(message.what);
    }

    /**
     * handler 回调
     */
    public interface Callback {
        public boolean handleMessage(Message msg);
    }
}
