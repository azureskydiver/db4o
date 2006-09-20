/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.inside;

public interface TestableReplicationProviderInside extends ReplicationProviderInside, SimpleObjectContainer {

	boolean supportsMultiDimensionalArrays();

	boolean supportsHybridCollection();

	boolean supportsRollback();

	boolean supportsCascadeDelete();
}
