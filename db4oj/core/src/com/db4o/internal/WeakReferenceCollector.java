/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.foundation.*;


/**
 * 
 */
class WeakReferenceCollector implements Runnable {
    
    final Object            _queue;
    private final ObjectContainerBase _stream;
    private SimpleTimer     _timer;
    public final boolean    _weak;

    WeakReferenceCollector(ObjectContainerBase a_stream) {
        _stream = a_stream;
        _weak = (!(a_stream instanceof TransportObjectContainer)
            && Platform4.hasWeakReferences() && a_stream.configImpl().weakReferences());
        _queue = _weak ? Platform4.createReferenceQueue() : null;
    }

    Object createYapRef(ObjectReference a_yo, Object obj) {
        
        if (!_weak) {  
            return obj;
        }
        
        return Platform4.createActiveObjectReference(_queue, a_yo, obj);
    }

    void pollReferenceQueue() {
        if (!_weak) {
        	return;
        }
        Platform4.pollReferenceQueue(_stream, _queue);
    }

    public void run() {
    	try {
    		pollReferenceQueue();
    	} catch (DatabaseClosedException dce) {
    		// can happen, no stack trace
    	} catch (Exception e) {
    		// don't bring down the thread
    		e.printStackTrace();
    	}
    }

    void startTimer() {
    	if (!_weak) {
    		return;
    	}
        
        if(! _stream.configImpl().weakReferences()){
            return;
        }
    	
        if (_stream.configImpl().weakReferenceCollectionInterval() <= 0) {
        	return;
        }

        if (_timer != null) {
        	return;
        }
        
        _timer = new SimpleTimer(this, _stream.configImpl().weakReferenceCollectionInterval(), "db4o WeakReference collector");
        _timer.start();
    }

    void stopTimer() {
    	if (_timer == null){
            return;
        }
        _timer.stop();
        _timer = null;
    }
    
}