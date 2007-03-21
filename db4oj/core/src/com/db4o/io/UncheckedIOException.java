package com.db4o.io;

import com.db4o.foundation.ChainedRuntimeException;

public class UncheckedIOException extends ChainedRuntimeException {

	public UncheckedIOException() {
		super();
	}

	public UncheckedIOException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public UncheckedIOException(String msg) {
		super(msg);
	}

	public UncheckedIOException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
