/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.template;

import com.db4o.foundation.Iterator4;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ProviderPair;
import com.db4o.test.replication.ReplicationTestcase;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;

public abstract class SimpleParentChild extends ReplicationTestcase {
	public SimpleParentChild() {
		super();
	}

	//TODO move to super class
	public void test() {
		final Iterator4 it = providerPairs.strictIterator();

		while (it.hasNext()) {
			ProviderPair p = (ProviderPair) it.next();
			init(p);
			printCombination(p);
			actualTst();
		}
		providerPairs = null;
	}

	private void actualTst() {
		clean();

		checkEmpty();

		storeParentAndChildToProviderA();

		replicateAllToProviderBFirstTime();

		modifyParentInProviderB();

		replicateAllStep2();

		modifyParentAndChildInProviderA();

		replicateParentClassStep3();

		clean();

		destroy();
	}

	protected void clean() {delete(new Class[]{SPCParent.class, SPCChild.class});}

	private void storeParentAndChildToProviderA() {
		SPCChild child = new SPCChild("c1");
		SPCParent parent = new SPCParent(child, "p1");
		_providerA.storeNew(parent);
		_providerA.commit();

		ensureNames(_providerA, "p1", "c1");
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureNames(_providerA, "p1", "c1");
		ensureNames(_providerB, "p1", "c1");
	}

	private void modifyParentInProviderB() {
		SPCParent parent = (SPCParent) getOneInstance(_providerB, SPCParent.class);
		parent.setName("p2");
		_providerB.update(parent);

		ensureNames(_providerB, "p2", "c1");
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureNames(_providerA, "p2", "c1");
		ensureNames(_providerB, "p2", "c1");
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

	private void replicateParentClassStep3() {
		replicateClass(_providerA, _providerB, SPCParent.class);

		ensureNames(_providerA, "p3", "c3");
		ensureNames(_providerB, "p3", "c3");
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

}
