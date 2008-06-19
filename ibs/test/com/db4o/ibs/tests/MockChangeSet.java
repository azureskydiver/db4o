package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ibs.*;

public class MockChangeSet implements ChangeSet {
		
	interface MockChange {
	}
	
	static class NewObjectChange implements MockChange {
		public NewObjectChange(Object object) {
		}
	}

	private final List<MockChange> _changes = new ArrayList<MockChange>();

	public List<MockChange> changes() {
		return _changes;
	}

	public void addNew(Object object) {
		_changes.add(new NewObjectChange(object));
	}
}
