/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class IsolationModeRequest extends CobraPersistentObject {
	
	private int _isolationMode;
	
	private boolean _isResponse;
	
	public IsolationModeRequest(int isolationMode) {
		_isolationMode = isolationMode;
		_isResponse = false;
	}
	
	public void isolationMode(int isolationMode) {
		_isolationMode = isolationMode;
	}

	public int isolationMode() {
		return _isolationMode;
	}
	
	public boolean isResponse() {
		return _isResponse;
	}
	
	public void isResponse(boolean isResponse) {
		_isResponse = isResponse;
	}
}
