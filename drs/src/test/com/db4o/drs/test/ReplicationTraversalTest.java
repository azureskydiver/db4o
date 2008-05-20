/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test;

import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.Replicated;

import db4ounit.*;

public class ReplicationTraversalTest implements TestLifeCycle {

	private TransientReplicationProvider _peerA;
	private TransientReplicationProvider _peerB;

	public void setUp() throws Exception {
		_peerA = new TransientReplicationProvider(new byte[]{0}, "A");
		_peerB = new TransientReplicationProvider(new byte[]{1}, "B");
		ReplicationReflector reflector = new ReplicationReflector(_peerA, _peerB);
		_peerA.replicationReflector(reflector);
		_peerB.replicationReflector(reflector);
	}
	
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

	public void tearDown() throws Exception {
	}

}
