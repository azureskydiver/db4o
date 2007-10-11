/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.core;

/**
 * @exclude
 */
public class InstrumentationStatus {

	public final static InstrumentationStatus FAILED = new InstrumentationStatus(false, false);
	public final static InstrumentationStatus INSTRUMENTED = new InstrumentationStatus(true, true);
	public final static InstrumentationStatus NOT_INSTRUMENTED = new InstrumentationStatus(true, false);
	
	private final boolean _canContinue;
	private final boolean _isInstrumented;
	
	private InstrumentationStatus(final boolean canContinue, final boolean isInstrumented) {
		_canContinue = canContinue;
		_isInstrumented = isInstrumented;
	}
	
	public boolean canContinue() {
		return _canContinue;
	}
	
	public boolean isInstrumented() {
		return _isInstrumented;
	}
	
	public InstrumentationStatus aggregate(InstrumentationStatus status) {
		if(!_canContinue || !status._canContinue) {
			return FAILED;
		}
		if(_isInstrumented || status._isInstrumented) {
			return INSTRUMENTED;
		}
		return NOT_INSTRUMENTED;
	}
}
