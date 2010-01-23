/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus;

public abstract class ErrorMessageSink {
	protected abstract void showError(String msg);
	protected abstract void logExc(Throwable exc);
	
	public void error(String msg)  {
		error(msg, null);
	}

	public void error(Throwable exc) {
		error(exc.getMessage(), exc);
	}
	
	public void error(String msg, Throwable exc) {
		showError(msg);
		if(exc != null) {
			logExc(exc);	
		}
	}
}