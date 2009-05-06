/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.foundation;

public class StopWatch {
	
	private long _started;
	private long _elapsed;

	public StopWatch() {
	}
	
	public void start() {
		_started = System.currentTimeMillis();
	}
	
	public void stop() {
		_elapsed = peek();
	}

	public long peek() {
		return System.currentTimeMillis() - _started;
	}
	
	public long elapsed() {
		return _elapsed;
	}
}
