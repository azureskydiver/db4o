package com.db4o.ibs.tests;

import com.db4o.ibs.*;
import com.db4o.internal.*;

public class MockChangeSetBuilder implements ChangeSetBuilder {
	
	private MockChangeSet _changeSet;
	
	public MockChangeSetBuilder() {
		reset();
	}

	public ChangeSet build(Transaction transaction) {
		ChangeSet changes = _changeSet;
		reset();
		return changes;
	}

	public void create(Transaction transaction, Object object) {
		_changeSet.addNew(object);
	}
	
	private void reset() {
		_changeSet = new MockChangeSet();
	}
}
