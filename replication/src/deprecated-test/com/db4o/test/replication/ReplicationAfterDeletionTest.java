/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;


public class ReplicationAfterDeletionTest extends ReplicationTestCase {

	protected void actualTest() {
		replicate();

		clean();
		replicate();
	}

	protected void clean() {delete(new Class[]{SPCChild.class, SPCParent.class});}

	private void replicate() {
		SPCChild child = new SPCChild("c1");
		SPCParent parent = new SPCParent(child, "p1");
		_providerA.storeNew(parent);
		_providerA.commit();

		replicateAll(_providerA, _providerB);
	}

	public void test() {
		super.test();
	}

}
