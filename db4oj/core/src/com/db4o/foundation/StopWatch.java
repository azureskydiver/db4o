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
