/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class TimestampSyncRequest extends CobraPersistentObject{
	
	private long _timestamp;
	
	private boolean _answered;
	
	private boolean _forceSync;
	
	public long timestamp() {
		return _timestamp;
	}
	
	public void timestamp(long timestamp) {
		_timestamp = timestamp;
	}
	
	public boolean isAnswered() {
		return _answered;
	}
	
	public void resetForRequest() {
		_timestamp = 0;
		_answered = false;
		_forceSync = false;
	}
	
	public void forceSync(boolean flag){
		_forceSync = flag;
	}
	
	public boolean forceSync(){
		return _forceSync;
	}
	
	public void answered(boolean flag){
		_answered = flag;
	}
}
