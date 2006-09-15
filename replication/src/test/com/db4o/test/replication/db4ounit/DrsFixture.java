/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.inside.replication.TestableReplicationProviderInside;

public interface DrsFixture {
	TestableReplicationProviderInside provider();

	void open();

	void close();

	void clean();
}
