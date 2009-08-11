/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring.internal;

import static com.db4o.foundation.Environments.my;

/**
 * @exclude
 */
@decaf.Ignore
public class TimedReading {
	
	private static final int IDENTITY_MS_FACTOR = 1;
	
	private final Clock _clock = my(Clock.class);
	
	private final int _msFactor;
	private int _count;
	private long _lastAccessTime = currentTimeMillis();

	public TimedReading() {
		this(IDENTITY_MS_FACTOR);
	}

	public TimedReading(int msFactor) {
		_msFactor = msFactor;
	}
	
	public void add(int count) {
		_count += count;
	}
	
	public double read() {
		long curTime = currentTimeMillis();
		long timeDiff = curTime - _lastAccessTime;
		
		double reading = timeDiff > 0
			? ((double)_count * _msFactor / timeDiff)
			: 0;
			
		_lastAccessTime = curTime;
		_count = 0;
		return reading;
	}
	
	private long currentTimeMillis() {
		return _clock.currentTimeMillis();
	}
}