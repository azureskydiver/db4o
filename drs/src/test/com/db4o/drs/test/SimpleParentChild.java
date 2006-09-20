/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.inside.replication.TestableReplicationProviderInside;

import db4ounit.Assert;

public class SimpleParentChild extends DrsTestCase {

	public void test() {

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

		Assert.areEqual(parent.getName(), parentName);
		Assert.areEqual(parent.getChild().getName(), childName);
	}

	private void ensureOneInstanceOfParentAndChild(TestableReplicationProviderInside provider) {
		ensureOneInstance(provider, SPCParent.class);
		ensureOneInstance(provider, SPCChild.class);
	}

	private void modifyParentAndChildInProviderA() {
		SPCParent parent = (SPCParent) getOneInstance(a().provider(), SPCParent.class);
		parent.setName("p3");
		SPCChild child = parent.getChild();
		child.setName("c3");
		a().provider().update(parent);
		a().provider().update(child);
		a().provider().commit();

		ensureNames(a().provider(), "p3", "c3");
	}

	private void modifyParentInProviderB() {
		SPCParent parent = (SPCParent) getOneInstance(b().provider(), SPCParent.class);
		parent.setName("p2");
		b().provider().update(parent);
		b().provider().commit();

		ensureNames(b().provider(), "p2", "c1");
	}

	private void replicateAllStep2() {
		replicateAll(b().provider(), a().provider());

		ensureNames(a().provider(), "p2", "c1");
		ensureNames(b().provider(), "p2", "c1");
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a().provider(), "p1", "c1");
		ensureNames(b().provider(), "p1", "c1");
	}

	private void replicateParentClassStep3() {
		replicateClass(a().provider(), b().provider(), SPCParent.class);

		ensureNames(a().provider(), "p3", "c3");
		ensureNames(b().provider(), "p3", "c3");
	}

	private void storeParentAndChildToProviderA() {
		SPCChild child = new SPCChild("c1");
		SPCParent parent = new SPCParent(child, "p1");
		a().provider().storeNew(parent);
		a().provider().commit();

		ensureNames(a().provider(), "p1", "c1");
	}

}
