package com.db4o.test.replication;

public class ReplicationEventTest extends ReplicationTestCase {
	protected void actualTest() {
		tstNoAction();
		tstOverrideWithA();
		tstOverrideWithB();
		tstStopTraversal();
	}

	private void tstStopTraversal() {
		throw new UnsupportedOperationException("fs");
	}

	private void tstOverrideWithB() {
		throw new UnsupportedOperationException("fs");
	}

	private void tstOverrideWithA() {
		throw new UnsupportedOperationException("fs");
	}

	private void tstNoAction() {
		throw new UnsupportedOperationException("fs");
	}

	protected void clean() {
	}
}
