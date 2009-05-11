package com.db4o.db4ounit.common.cs;

import db4ounit.*;

public class PrefetchConfigurationTestCase extends ClientServerTestCaseBase {
	
	public void testDefaultPrefetchDepth() {
		Assert.areEqual(0, client().config().prefetchDepth());
	}

}
