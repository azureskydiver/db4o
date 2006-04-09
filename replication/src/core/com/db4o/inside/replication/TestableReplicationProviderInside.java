/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

public interface TestableReplicationProviderInside extends ReplicationProviderInside, SimpleObjectContainer {

	boolean supportsMultiDimensionalArrays();

	boolean supportsHybridCollection();

	boolean supportsRollback();
}
