package com.db4o.ibs.engine;

import java.util.*;

import com.db4o.ibs.*;

public class SlotBasedChangeSet implements ChangeSet {
	
	private List<SlotBasedChange> _changes;

	public SlotBasedChangeSet(List<SlotBasedChange> changes) {
		_changes = changes;
	}

	public List<SlotBasedChange> changes() {
		return _changes;
	}
}
