/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;


public class ReplicationAfterDeletionTest extends DrsTestCase {

	public void test() {
		replicate();
		clean();
		replicate();
	}

	protected void clean() {
		delete(new Class[]{SPCChild.class, SPCParent.class});
	}

	private void replicate() {
		SPCChild child = new SPCChild("c1");
		SPCParent parent = new SPCParent(child, "p1");
		a().provider().storeNew(parent);
		a().provider().commit();

		replicateAll(a().provider(), b().provider());
	}

}
