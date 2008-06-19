package com.db4o.ibs.tests;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.ibs.*;
import com.db4o.internal.*;

public class MockChangeSetEngine implements ChangeSetEngine {

	public ChangeSetBuilder newBuilderFor(Transaction transaction) {
		return new MockChangeSetBuilder();
	}

	public ChangeSetProcessor newProcessorFor(ObjectContainer container) {
		throw new NotImplementedException();
	}

}
