package com.db4o.drs.versant.ipc.inband;

public class TimestampSyncRequest {
	private long _timestamp;
	
	public long timestamp() {
		return _timestamp;
	}
	
	public void timestamp(long timestamp) {
		_timestamp = timestamp;
	}
	
	public boolean isAnswered() {
		return _timestamp > 0;
	}
	
	public void resetForRequest() {
		_timestamp = 0;
	}
}
