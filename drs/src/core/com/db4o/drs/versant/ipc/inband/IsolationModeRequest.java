package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.metadata.*;

public class IsolationModeRequest {
	private IsolationMode _isolationMode;
	private boolean _isResponse;
	
	public IsolationModeRequest(IsolationMode isolationMode) {
		_isolationMode = isolationMode;
		_isResponse = false;
	}
	
	public void isolationMode(IsolationMode isolationMode) {
		_isolationMode = isolationMode;
	}

	public IsolationMode isolationMode() {
		return _isolationMode;
	}
	
	public boolean isResponse() {
		return _isResponse;
	}
	
	public void isResponse(boolean isResponse) {
		_isResponse = isResponse;
	}
}
