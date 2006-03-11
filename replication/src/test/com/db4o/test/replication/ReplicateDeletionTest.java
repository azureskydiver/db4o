package com.db4o.test.replication;

public abstract class ReplicateDeletionTest extends ReplicationTestcase {
	//TODO
	public void test() {
		init();

		clean();

		storeObjectToProviderA();

		replicateAllToProviderBFirstTime();

		deleteObjectInProviderB();

		replicateAllToProviderA();

		clean();

		destroy();
	}

	private void replicateAllToProviderA() {
		throw new UnsupportedOperationException("todo implement");
	}

	private void deleteObjectInProviderB() {
		throw new UnsupportedOperationException("todo implement");
	}

	private void replicateAllToProviderBFirstTime() {
		throw new UnsupportedOperationException("todo implement");
	}

	private void storeObjectToProviderA() {
		throw new UnsupportedOperationException("todo implement");
	}

	private void clean() {
		throw new UnsupportedOperationException("todo implement");
	}
}
