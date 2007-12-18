/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;


public class NanoStopWatch {

		
	private long _started;
	private long _elapsed;

	public NanoStopWatch() {
	}
	
	public void start() {
		_started = System.nanoTime();
	}
	
	public void stop() {
		_elapsed = System.nanoTime() - _started;
	}
	
	public long elapsed() {
//		System.out.println("NanoStopWatch.elapsed: " + _elapsed);
		return _elapsed;
	}
	
}
