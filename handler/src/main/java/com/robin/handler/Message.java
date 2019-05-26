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
