package com.db4o.test.replication;

public abstract class ReplicateDeletionTest extends ReplicationTestcase {
	public void test() {
		init();

		clean();

		storeObjectToProviderA();

		replicateAllToProviderBFirstTime();

		deleteObjectInProviderB();

		replicateAllToProviderA();

		clean();
	}

	private void replicateAllToProviderA() {
		throw new UnsupportedOperationException("fs");
	}

	private void deleteObjectInProviderB() {
		throw new UnsupportedOperationException("fs");
	}

	private void replicateAllToProviderBFirstTime() {
		throw new UnsupportedOperationException("fs");
	}

	private void storeObjectToProviderA() {
		throw new UnsupportedOperationException("fs");
	}

	private void clean() {
		throw new UnsupportedOperationException("fs");
	}
}
