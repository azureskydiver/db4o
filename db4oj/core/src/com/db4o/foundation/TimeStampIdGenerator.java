/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class TimeStampIdGenerator {
    
	private long _last;

	public static long idToMilliseconds(long id) {
		return id >> 15;
	}

	public TimeStampIdGenerator() {
		this(0);
	}

	public TimeStampIdGenerator(long minimumNext) {
		_last = minimumNext;
	}

	public long generate() {
		long t = System.currentTimeMillis();

		t = t << 15;

		if (t <= _last) {
			_last ++;
		} else {
			_last = t;
		}
		return _last;
	}

	public long last() {
		return _last;
	}

	public boolean setMinimumNext(long newMinimum) {
        if(newMinimum <= _last){
            return false;
        }
		_last = newMinimum;
        return true;
	}
}
