package com.robin.handler;

import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Modifier;

/**
 *  Android对handler的解释
 *
 *
 * A Handler allows you to send and process {@link Message} and Runnable
 * objects associated with a thread's {@link MessageQueue}.  Each Handler
 * instance is associated with a single thread and that thread's message
 * queue.  When you create a new Handler, it is bound to the thread /
 * message queue of the thread that is creating it -- from that point on,
 * it will deliver messages and runnables to that message queue and execute
 * them as they come out of the message queue.
 *
 * <p>There are two main uses for a Handler: (1) to schedule messages and
 * runnables to be executed as some point in the future; and (2) to enqueue
 * an action to be performed on a different thread than your own.
 *
 * <p>Scheduling messages is accomplished with the
 * {@link #post}, {@link #postAtTime(Runnable, long)},
 * {@link #postDelayed}, {@link #sendEmptyMessage},
 * {@link #sendMessage}, {@link #sendMessageAtTime}, and
 * {@link #sendMessageDelayed} methods.  The <em>post</em> versions allow
 * you to enqueue Runnable objects to be called by the message queue when
 * they are received; the <em>sendMessage</em> versions allow you to enqueue
 * a {@link Message} object containing a bundle of data that will be
 * processed by the Handler's {@link #handleMessage} method (requiring that
 * you implement a subclass of Handler).
 *
 * <p>When posting or sending to a Handler, you can either
 * allow the item to be processed as soon as the message queue is ready
 * to do so, or specify a delay before it gets processed or absolute time for
 * it to be processed.  The latter two allow you to implement timeouts,
 * ticks, and other timing-based behavior.
 *
 * <p>When a
 * process is created for your application, its main thread is dedicated to
 * running a message queue that takes care of managing the top-level
 * application objects (activities, broadcast receivers, etc) and any windows
 * they create.  You can create your own threads, and communicate back with
 * the main application thread through a Handler.  This is done by calling
 * the same <em>post</em> or <em>sendMessage</em> methods as before, but from
 * your new thread.  The given Runnable or Message will then be scheduled
 * in the Handler's message queue and processed when appropriate.
 */
public class HandlerLite {
    private static final String TAG = "HandlerLite";
    //此标志用于检测重写的handler是否是非静态的内部类/匿名类，非静态可能会导致内存泄漏
    private static final boolean FIND_POTENTIAL_LEAKS = false;

    private Looper myLooper;
    private MessageQueue mQueue;
    private Callback mCallback;
    Runnable callback;

    /**
     * handler 本身的回调
     */
    public interface Callback{
        public boolean handleMessage(Message msg);
    }

    /**
     * 构造函数
     */
    public HandlerLite(){
      this(null);
    }

    /**
     *
     * @param callback
     */
    public HandlerLite(Callback callback){
        //检测是否有内存泄漏的可能（PS: 由此可知在使用handler时需要声明为静态）
        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends HandlerLite> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                        klass.getCanonicalName());
            }
        }
        //获取looper实例
        myLooper=Looper.myLooper();
        if (myLooper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = myLooper.mQueue;
        mCallback = callback;
    }

    /**
     *  从message缓存池中获取一个message 推荐用此方法获取message实例或者直接调用Message.obtain()
     * @return
     */
    public Message obtainMessage(){
        return Message.obtain();
    }

    /**
     * handler 必须重写的方法
     * @param msg
     */
    public void handleMessage(Message msg){

    }

    public final boolean sendMessage(Message msg){
       return sendMessageDelayed(msg,0);
    }

    public final boolean post(Runnable r)
    {
        return  sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis)
    {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis)
    {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
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

    //=======================以下方法仅是对Android os的仿写 handler流程并未使用=================

    /**
     *
     * @param message
     * @return  mesage name if callback is not null return the class name of the
     *       message callback else return he hexadecimal representation of the
     *       message "what" field.
     */
    public String getMessageName(Message message) {
        if (message.callback!= null) {
            return message.callback.getClass().getName();
        }
        return "0x" + Integer.toHexString(message.what);
    }
}
