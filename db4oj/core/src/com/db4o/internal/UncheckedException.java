package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class UncheckedException extends ChainedRuntimeException {

	public UncheckedException() {
		super();
	}

	public UncheckedException(String msg) {
		super(msg);
	}
	
	public UncheckedException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
