package com.robin.handler;

public class Message {

    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    //

    public long when;
    public int flags;
    HandlerLite target;
    private static final Object sPoolSync = new Object();
    private static Message sPool;
    Message next;
    private static int sPoolSize = 0;
    static final int FLAG_IN_USE = 1 << 0;
    Runnable callback;
    private static final int MAX_POOL_SIZE = 50;

    private static boolean gCheckRecycle = true;

    public Message() {
    }

    public static Message obtain(){
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();

    }

    public HandlerLite getTarget() {
        return target;
    }

    public void setTarget(HandlerLite target) {
        this.target = target;
    }

    /*package*/ boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    /*package*/ void markInUse() {
        flags |= FLAG_IN_USE;
    }

    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it "
                        + "is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        when = 0;
        target = null;
        callback = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "what=" + what +
                ", arg1=" + arg1 +
                ", arg2=" + arg2 +
                ", obj=" + obj.toString() +
                ", when=" + when +
                ", flags=" + flags +
                ", target=" + target +
                '}';
    }
}
