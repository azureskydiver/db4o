/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.util;

public class StopWatch {
	
	private long _started;

	private long _finished;
	
	public StopWatch() {
	}
	
	public void start() {
		_started = System.currentTimeMillis();
	}
	
	public void stop() {
		_finished = System.currentTimeMillis();
	}
	
	public long elapsed() {
		return _finished - _started;
	}
	
	public String toString() {
		return elapsed() + "ms";
	}
}
