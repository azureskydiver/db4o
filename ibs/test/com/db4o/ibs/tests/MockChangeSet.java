package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ibs.*;

public class MockChangeSet implements ChangeSet {
		
	public interface MockChange {
	}
	
	public static class NewObjectChange implements MockChange {
	}
	
	public static class UpdateObjectChange implements MockChange {
	}
	
	public static class DeleteObjectChange implements MockChange {
	}

	private final List<MockChange> _changes = new ArrayList<MockChange>();

	public List<MockChange> changes() {
		return _changes;
	}

	public void addNew(Object object) {
		_changes.add(new NewObjectChange());
	}

	public void addDeleted(Object object) {
		_changes.add(new DeleteObjectChange());
	}

	public void addUpdated(Object object) {
		_changes.add(new UpdateObjectChange());
	}
}
