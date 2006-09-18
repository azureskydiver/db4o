package com.db4o.test.replication;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;

public class Simplest extends ReplicationTestCase {
	public void test() {
		super.test();
	}

	protected void actualTest() {
		storeToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		modifyInProviderA();

		replicateStep3();
	}

	private void ensureNames(TestableReplicationProviderInside provider, String childName) {
		ensureOneInstance(provider, SPCChild.class);
		SPCChild child = getTheObject(provider);
		Test.ensureEquals(childName,child.getName());
	}

	private SPCChild getTheObject(TestableReplicationProviderInside provider) {
		return (SPCChild) getOneInstance(provider, SPCChild.class);
	}

	private void modifyInProviderA() {
		SPCChild child = getTheObject(_providerA);

		child.setName("c3");

		_providerA.update(child);
		_providerA.commit();

		ensureNames(_providerA, "c3");
	}

	private void modifyInProviderB() {
		SPCChild child = getTheObject(_providerB);

		child.setName("c2");
		_providerB.update(child);
		_providerB.commit();

		ensureNames(_providerB, "c2");
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureNames(_providerA, "c2");
		ensureNames(_providerB, "c2");
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureNames(_providerA, "c1");
		ensureNames(_providerB, "c1");
	}

	private void replicateStep3() {
		//System.out.println("BEGIN DEBUG");
		replicateClass(_providerA, _providerB, SPCChild.class);
		//System.out.println("END DEBUG");


		ensureNames(_providerA, "c3");
		ensureNames(_providerB, "c3");
	}

	private void storeToProviderA() {
		SPCChild child = new SPCChild("c1");
		
		_providerA.storeNew(child);
		_providerA.commit();

		ensureNames(_providerA, "c1");
	}
}
