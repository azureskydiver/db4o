/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

public class SimpleTimer implements Runnable {
	
	private final Runnable _runnable;
	private final int _interval;
	
	// need public for C# compilation
	// Carl, why does C# need public here?
    // Klaus, I think the old converter needed it, we can remove that after we run Rodrigo's. 
	public volatile boolean stopped = false;
	
	public SimpleTimer(Runnable runnable, int interval, String name){
		_runnable = runnable;
		_interval = interval;
		Thread thread = new Thread(this);
		thread.setName(name);
		thread.start();
		//Carl, shouldn't we set this as a daemon:  thread.setDaemon(true);  ?
        // Klaus, in principle yes, but don't forget to implement setDaemon() for .NET. 
	}
	
	public void stop(){
		stopped = true;
	}
	
	public void run() {
		while(! stopped){
			Cool.sleepIgnoringInterruption(_interval);
            if(! stopped){
				_runnable.run();
            }
		}
    }
}
