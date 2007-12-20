/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.timing;


public class NanoStopWatch {

		
	private long _started;
	private long _elapsed;
	
	private NanoTiming _timing;

	public NanoStopWatch() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		_timing = NanoTimingInstance.newInstance();
	}
	
	public void start() {
		_started = _timing.nanoTime();
	}
	
	public void stop() {
		_elapsed = _timing.nanoTime() - _started;
	}
	
	public long elapsed() {
//		System.out.println("NanoStopWatch.elapsed: " + _elapsed);
		return _elapsed;
	}
	
}
