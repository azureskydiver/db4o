/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus;

public interface ErrorMessageSink {
	void error(String msg);
	void exc(Throwable exc);
}