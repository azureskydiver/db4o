/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.types.*;

class Timer4 implements Runnable, TransientClass{
	
	private final Runnable runnable;
	private final int interval;
	
	// need public for C# compilation
	public volatile boolean stopped = false;
	
	Timer4(Runnable runnable, int interval, String name){
		this.runnable = runnable;
		this.interval = interval;
	    if(runnable != null){
			Thread thread = new Thread(this);
			thread.setName(name);
			thread.start();
	    }
	    
	}
	
	void stop(){
		stopped = true;
	}
	
	public void run() {
		while(! stopped){
			try {
                Thread.sleep(interval);
            } catch (Exception e) {
            }
            if(! stopped){
				runnable.run();
            }
		}
    }
}
