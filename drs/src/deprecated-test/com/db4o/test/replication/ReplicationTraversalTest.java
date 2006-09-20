/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.GenericReplicationSession;
import com.db4o.test.Test;
import com.db4o.test.replication.transients.TransientReplicationProvider;

public class ReplicationTraversalTest {

	private TransientReplicationProvider _peerA = new TransientReplicationProvider(new byte[]{0});
	private TransientReplicationProvider _peerB = new TransientReplicationProvider(new byte[]{1});

	public void test() {
		Replicated obj1 = new Replicated("1");
		Replicated obj2 = new Replicated("2");
		Replicated obj3 = new Replicated("3");

		obj1.setLink(obj2);
		obj2.setLink(obj3);
		obj3.setLink(obj1);

		_peerA.storeNew(obj1);
		//_peerA.transientProviderSpecificStore(obj2);
		//_peerA.transientProviderSpecificStore(obj3);

		ReplicationSession replication = new GenericReplicationSession(_peerA, _peerB);
		replication.replicate(obj1);

		Test.ensure(_peerA.activatedObjects().containsKey(obj1));
		Test.ensure(_peerA.activatedObjects().containsKey(obj2));
		Test.ensure(_peerA.activatedObjects().containsKey(obj3));

		_peerA = null;
		_peerB = null;
	}
}
