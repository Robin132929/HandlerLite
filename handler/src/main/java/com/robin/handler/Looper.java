package com.robin.handler;

public class Looper {

    MessageQueue mQueue;
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    public Looper(){
      mQueue=new MessageQueue();
    }

    public static void prepare(){
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper(){
        return sThreadLocal.get();
    }

    public static void loop(){
        final Looper me=myLooper();
        if (me==null){
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue=me.mQueue;

        for (;;){
            Message msg=queue.next();
            if (msg==null){
                return;
            }
            msg.target.dispatchMessage(msg);
        }

    }

    public void quit() {
        mQueue.quit(false);
    }

    public void quitSafely() {
        mQueue.quit(true);
    }

}
