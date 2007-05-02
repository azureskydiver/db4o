/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.timestamp;

import java.sql.Timestamp;


public class TimeKeeper {
	Timestamp _timestamp;
	
	public TimeKeeper(Timestamp timestamp) {
		_timestamp = timestamp;
	}
	
	public String toString() {
		return _timestamp.toString();
	}
}
