/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

public class Timer {
	private long start;

	private long end;

	public void start() {
		start = System.currentTimeMillis();
	}

	public long stop() {
		end = System.currentTimeMillis();
		return end - start;
	}

	public long elapsed() {
		return end - start;
	}
}
