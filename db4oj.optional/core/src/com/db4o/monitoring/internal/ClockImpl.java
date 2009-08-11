/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring.internal;

/**
 * @exclude
 */
public class ClockImpl implements Clock {

	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

}
