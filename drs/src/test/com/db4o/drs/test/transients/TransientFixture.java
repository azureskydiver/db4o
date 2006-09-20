/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test.transients;

import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.DrsFixture;

public class TransientFixture implements DrsFixture {
	private String _name;
	
	private TestableReplicationProviderInside _provider;
	
	public TransientFixture(String name) {
		_name = name;
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		//do nothing
	}

	public void close() {
		_provider.destroy();
	}

	public void open()  {
		_provider = new TransientReplicationProvider(new byte[]{65}, _name);
	}
}
