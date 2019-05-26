package com.robin.handler;

import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Modifier;

public class HandlerLite {
    private static final String TAG = "HandlerLite";
    private static final boolean FIND_POTENTIAL_LEAKS = false;

    private Looper myLooper;
    private MessageQueue mQueue;
    private Callback mCallback;
    Runnable callback;

    public interface Callback{
        public boolean handleMessage(Message msg);
    }

    public HandlerLite(){

        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends HandlerLite> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                        klass.getCanonicalName());
            }
        }
        myLooper=Looper.myLooper();
        if (myLooper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = myLooper.mQueue;
        mCallback = null;
    }

    public Message obtainMessage(){
        return Message.obtain();
    }

    public void handleMessage(Message msg){

    }

    public final boolean sendMessage(Message msg){
       return sendMessageDelayed(msg,0);
    }

    private boolean sendMessageDelayed(Message msg, long delay) {
        if (delay<0){
            delay=0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis()+ delay);
    }


    public final boolean sendMessageAtTime(Message msg,long delay){
        MessageQueue messageQueue=mQueue;
        if (messageQueue==null){
            RuntimeException e=new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;

        }
        return enQueueMessage(messageQueue,msg,delay);
    }

    private boolean enQueueMessage(MessageQueue messageQueue, Message msg, long delay) {
        msg.target=this;
        return messageQueue.enQueueMessage(msg,delay);
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

    private static void handleCallback(Message message) {
        message.callback.run();
    }
}
