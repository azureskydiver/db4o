package com.db4o.drs.db4o;

import com.db4o.drs.inside.TestableReplicationProvider;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.internal.replication.Db4oReplicationReferenceProvider;

public interface Db4oReplicationProvider 
	extends TestableReplicationProvider, Db4oReplicationReferenceProvider, TestableReplicationProviderInside{

	public ExtObjectContainer getObjectContainer();

}
