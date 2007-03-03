package com.db4o.io;

import com.db4o.internal.UncheckedException;

public class UncheckedIOException extends UncheckedException {

	public UncheckedIOException(Throwable cause) {
		super(cause);
	}

	public UncheckedIOException() {
		super();
	}

	public UncheckedIOException(String msg) {
		super(msg);
	}

}
