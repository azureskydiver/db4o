package com.db4o.ibs.tests;

import com.db4o.ext.*;
import com.db4o.ibs.*;

public class MockChangeSetBuilder implements ChangeSetBuilder {
	
	private final MockChangeSet _changeSet = new MockChangeSet();

	public ChangeSet build() {
		if (_changeSet.changes().isEmpty()) {
			return null;
		}
		return _changeSet;
	}

	public void added(ObjectInfo object) {
		_changeSet.addNew(object);
	}
	
	public void deleted(ObjectInfo object) {
		_changeSet.addDeleted(object);
	}

	public void updated(ObjectInfo object) {
		_changeSet.addUpdated(object);
	}
}
