/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.SimpleTimer;

/**
 * 
 */
class YapReferences implements Runnable {
    
    final Object            _queue;
    private final YapStream _stream;
    private SimpleTimer     _timer;
    public final boolean    _weak;

    YapReferences(YapStream a_stream) {
        _stream = a_stream;
        _weak = (!(a_stream instanceof YapObjectCarrier)
            && Platform.hasWeakReferences() && a_stream.i_config.i_weakReferences);
        _queue = _weak ? Platform.createReferenceQueue() : null;
    }

    Object createYapRef(YapObject a_yo, Object obj) {
        if (!_weak) {  //TODO Klaus: consider a NullObject or testing for null in the caller.
            return obj;
        }
        return Platform.createYapRef(_queue, a_yo, obj);
    }

    public void run() {
//        if (_weak) {   //Carl, this "if" is unnecessary because the timer will only be created if _weak. If you agree, please remove it.
            Platform.pollReferenceQueue(_stream, _queue);
//        }
    }

    void startTimer() {
    	if (!_weak) {
    		return;
    	}
    	
        if (_stream.i_config.i_weakReferenceCollectionInterval <= 0) {
        	return;
        }

        if (_timer != null) {
        	return; //TODO Consider throwing an IllegalStateException here to detect design problems.
        }
        
        _timer = new SimpleTimer(this, _stream.i_config.i_weakReferenceCollectionInterval, "db4o WeakReference collector");
    }

    void stopTimer() {
    	if (_timer == null) return;
        _timer.stop();
        _timer = null;
    }
    
}