/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

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
		_elapsed = System.currentTimeMillis() - _started;
	}
	
	public long elapsed() {
		return _elapsed;
	}
}
