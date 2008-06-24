package com.db4o.ibs.tests.mocking;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.ibs.*;

public class MockChangeSetListener implements ChangeSetListener {

	private final List<ChangeSet> _changeSets = new ArrayList<ChangeSet>();

	public void onChange(ChangeSet changes) {
		if (null == changes) {
			throw new ArgumentNullException();
		}
		_changeSets.add(changes);
	}

	public List<ChangeSet> changeSets() {
		return _changeSets;
	}

}
