/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * 
 */
class YapReferences implements Runnable {
    
    final Object            i_queue;
    private final YapStream i_stream;
    private Timer4          i_timer;
    public final boolean    i_weak;

    YapReferences(YapStream a_stream) {
        i_stream = a_stream;
        i_weak = (!(a_stream instanceof YapObjectCarrier)
            && Platform.hasWeakReferences() && a_stream.i_config.i_weakReferences);
        if (i_weak) {
            i_queue = Platform.createReferenceQueue();
        } else {
            i_queue = null;
        }
    }

    Object createYapRef(YapObject a_yo, Object obj) {
        if (!i_weak) {
            return obj;
        }
        return Platform.createYapRef(i_queue, a_yo, obj);
    }

    public void run() {
        if (i_weak) {
            Platform.pollReferenceQueue(i_stream, i_queue);
        }
    }

    void startTimer() {
        if (i_weak && i_stream.i_config.i_weakReferenceCollectionInterval > 0) {
            if (i_timer == null || i_timer.stopped) {
                i_timer = new Timer4(this,
                    i_stream.i_config.i_weakReferenceCollectionInterval,
                    "db4o WeakReference collector");
            }
        }
    }

    void stopTimer() {
        if (i_timer != null) {
            i_timer.stop();
        }
    }

}