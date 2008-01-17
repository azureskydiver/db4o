/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.tp;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.ta.*;

import db4ounit.extensions.mocking.*;

public class RollbackStrategyMock implements RollbackStrategy {
	
	private MethodCallRecorder _recorder = new MethodCallRecorder();

	public void rollback(ObjectContainer container, ObjectInfo o) {
		_recorder.record(new MethodCall("rollback", container, o));
	}
	
	public void verify(MethodCall[] expectedCalls) {
		_recorder.verify(expectedCalls);
	}
}
