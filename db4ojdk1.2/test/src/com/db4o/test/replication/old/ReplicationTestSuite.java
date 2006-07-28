/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.old;

import com.db4o.test.*;

public class ReplicationTestSuite extends TestSuite{

	public Class[] tests() {
		return new Class[] {
            MigrateFromNull.class,
            ReplicateDb4oList.class,
			// ReplicationFeaturesMain.class,
            R0to4Runner.class,
			ReplicationFeatures.class,
			ReplicateExistingFile.class
		};
	}
	
	

}
