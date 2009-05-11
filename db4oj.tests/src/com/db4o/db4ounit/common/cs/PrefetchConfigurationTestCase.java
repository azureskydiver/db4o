package com.db4o.db4ounit.common.cs;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class PrefetchConfigurationTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS{
	
	public void testDefaultPrefetchDepth() {
		Assert.areEqual(0, client().config().prefetchDepth());
	}

}
