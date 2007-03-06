package com.db4o.io;

import com.db4o.foundation.Db4oRuntimeException;

public class UncheckedIOException extends Db4oRuntimeException {

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
		super(cause);
	}


}
