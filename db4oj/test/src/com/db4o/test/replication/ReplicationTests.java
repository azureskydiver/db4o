package com.db4o.test.replication;

import com.db4o.test.*;

public class ReplicationTests extends TestSuite{

	public Class[] tests() {
		return new Class[] {
				ReplicationFeatures.class,
				ReplicationFeaturesMain.class
		};
	}
	
	

}
