package com.robin.handler;

import android.os.SystemClock;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MessageQueue {
    Message mMessages;

    public MessageQueue() {

    }

    public Message next() {
        int nextPollTimeoutMillis = 0;
        for (; ; ) {
            synchronized (this) {
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = mMessages;
                if (msg != null && msg.target == null) {
                    Log.i(TAG, "next: "+msg.target);
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null);
                }
                if (msg != null) {
                    if (now < msg.when) {
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                    } else {

                        if (prevMsg != null) {
                            Log.i(TAG, "next pre: "+prevMsg.toString());
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        msg.markInUse();
                        Log.i(TAG, "next: " + msg.toString());
                        return msg;
                    }
                } else {
                    // No more messages.
                    nextPollTimeoutMillis = -1;
                }
            }
        }

    }

    public boolean enQueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }
        synchronized (this) {
            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            if (p == null || when == 0 || when < p.when) {
                msg.next = p;
                mMessages = msg;
            } else {
                Message prev;
                for (; ; ) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }
        }
        return true;
    }

    void removeCallbacksAndMessages(HandlerLite h, Object object) {

    }
}
