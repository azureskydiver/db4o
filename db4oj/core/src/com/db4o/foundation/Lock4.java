/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * @sharpen.ignore
 */
public class Lock4 {

    public Object run(Closure4 closure) {
    	synchronized(this){
    		return closure.run();
    	}
    }

    public void snooze(long timeout) {
    	try {
            wait(timeout);
        } catch (Exception e) {
        }
        
    }

    public void awake() {
    	notifyAll();
    }
}
