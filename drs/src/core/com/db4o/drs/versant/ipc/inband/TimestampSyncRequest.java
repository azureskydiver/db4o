/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class TimestampSyncRequest extends CobraPersistentObject{
	
	private long _timestamp;
	
	public long timestamp() {
		return _timestamp;
	}
	
	public void timestamp(long timestamp) {
		_timestamp = timestamp;
	}
	
}
