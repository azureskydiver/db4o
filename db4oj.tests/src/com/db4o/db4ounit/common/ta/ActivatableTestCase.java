/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import db4ounit.extensions.mocking.*;

public class ActivatableTestCase extends TransparentActivationTestCaseBase {
	
	public void testActivatorIsBoundUponStore() {
		
		final MockActivatable mock = new MockActivatable();
		store(mock);
		assertSingleBindCall(mock);
	}

	private void assertSingleBindCall(final MockActivatable mock) {
		mock.recorder().verify(new MethodCall[] {
			new MethodCall("bind", MethodCall.IGNORED_ARGUMENT)
		});
	}
	
	public void testActivatorIsBoundUponRetrieval() throws Exception {
		
		store(new MockActivatable());
		reopen();
		assertSingleBindCall(retrieveMock());
	}

	private MockActivatable retrieveMock() {
		return (MockActivatable) retrieveOnlyInstance(MockActivatable.class);
	}
}
