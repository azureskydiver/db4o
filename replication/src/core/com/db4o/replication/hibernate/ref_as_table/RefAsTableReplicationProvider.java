package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.AbstractReplicationProvider;
import com.db4o.replication.hibernate.RefConfig;
import org.hibernate.Session;

public abstract class RefAsTableReplicationProvider extends AbstractReplicationProvider {
	protected ObjectConfig _objectCfg;

	protected RefConfig _refCfg;

	protected Session _objectSession;

	protected Session _refSession;
}
