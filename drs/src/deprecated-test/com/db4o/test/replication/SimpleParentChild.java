/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.test.Test;

public class SimpleParentChild extends ReplicationTestCase {

	protected void clean() {delete(new Class[]{SPCParent.class, SPCChild.class});}

	protected void actualTest() {

		storeParentAndChildToProviderA();

		replicateAllToProviderBFirstTime();

		modifyParentInProviderB();

		replicateAllStep2();

		modifyParentAndChildInProviderA();

		replicateParentClassStep3();
	}

	private void ensureNames(TestableReplicationProviderInside provider, String parentName, String childName) {
		ensureOneInstanceOfParentAndChild(provider);
		SPCParent parent = (SPCParent) getOneInstance(provider, SPCParent.class);

		if (! parent.getName().equals(parentName)) {
			System.out.println("expected = " + parentName);
			System.out.println("actual = " + parent.getName());
		}

		Test.ensure(parent.getName().equals(parentName));
		Test.ensure(parent.getChild().getName().equals(childName));
	}

	private void ensureOneInstanceOfParentAndChild(TestableReplicationProviderInside provider) {
		ensureOneInstance(provider, SPCParent.class);
		ensureOneInstance(provider, SPCChild.class);
	}

	private void modifyParentAndChildInProviderA() {
		SPCParent parent = (SPCParent) getOneInstance(_providerA, SPCParent.class);
		parent.setName("p3");
		SPCChild child = parent.getChild();
		child.setName("c3");
		_providerA.update(parent);
		_providerA.update(child);
		_providerA.commit();

		ensureNames(_providerA, "p3", "c3");
	}

	private void modifyParentInProviderB() {
		SPCParent parent = (SPCParent) getOneInstance(_providerB, SPCParent.class);
		parent.setName("p2");
		_providerB.update(parent);
		_providerB.commit();

		ensureNames(_providerB, "p2", "c1");
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureNames(_providerA, "p2", "c1");
		ensureNames(_providerB, "p2", "c1");
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureNames(_providerA, "p1", "c1");
		ensureNames(_providerB, "p1", "c1");
	}

	private void replicateParentClassStep3() {
		replicateClass(_providerA, _providerB, SPCParent.class);

		ensureNames(_providerA, "p3", "c3");
		ensureNames(_providerB, "p3", "c3");
	}

	private void storeParentAndChildToProviderA() {
		SPCChild child = new SPCChild("c1");
		SPCParent parent = new SPCParent(child, "p1");
		_providerA.storeNew(parent);
		_providerA.commit();

		ensureNames(_providerA, "p1", "c1");
	}

	public void test() {
		super.test();
	}
}
