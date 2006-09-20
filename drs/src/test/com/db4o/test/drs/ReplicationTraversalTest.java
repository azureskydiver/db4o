/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs;

import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.drs.Replicated;

import db4ounit.Assert;

public class ReplicationTraversalTest extends DrsTestCase {

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

		Assert.isTrue(_peerA.activatedObjects().containsKey(obj1));
		Assert.isTrue(_peerA.activatedObjects().containsKey(obj2));
		Assert.isTrue(_peerA.activatedObjects().containsKey(obj3));

		_peerA = null;
		_peerB = null;
	}

}
