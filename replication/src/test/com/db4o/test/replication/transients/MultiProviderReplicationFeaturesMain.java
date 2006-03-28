package com.db4o.test.replication.transients;

import com.db4o.inside.replication.*;
import com.db4o.test.replication.*;
import com.db4o.test.replication.db4o.*;

public class MultiProviderReplicationFeaturesMain extends ReplicationFeaturesMain {

    private TestableReplicationProviderInside _a = Db4oReplicationTestUtil.newProviderA();
	private TestableReplicationProviderInside _b;

	protected TestableReplicationProviderInside prepareProviderA() {
		return _a;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return _b;
	}

	public void testDb4oCombinedWithAllOthers() {
        tstCombination(new TransientReplicationProvider(new byte[] {66}, "B"));
        tstCombination(Db4oReplicationTestUtil.newProviderB());
//      tstCombination(Hibernate1);
//      tstCombination(Hibernate2);
//      tstCombination(Hibernate3);
    }

    private void tstCombination(TestableReplicationProviderInside peerB) {
        _b = peerB;
        System.out.println("Combination: A " + _a + "   - B " + _b);
        super.test();
    }
    
}
