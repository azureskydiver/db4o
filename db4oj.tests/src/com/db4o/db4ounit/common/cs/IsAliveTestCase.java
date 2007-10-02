package com.db4o.db4ounit.common.cs;

import com.db4o.internal.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class IsAliveTestCase  extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS {

	public void _test() {
		Assert.isTrue(((ClientObjectContainer)db()).isAlive());
	}
	
}
