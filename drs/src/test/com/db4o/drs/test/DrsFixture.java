/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.drs.inside.TestableReplicationProviderInside;

public interface DrsFixture {
	TestableReplicationProviderInside provider();

	void open();

	void close();

	void clean();
}
