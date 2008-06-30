package com.db4o.ibs.engine;

import com.db4o.*;
import com.db4o.ibs.*;
import com.db4o.internal.*;

public class SlotBasedChangeSetEngine implements ChangeSetEngine {

	public ChangeSetBuilder newBuilderFor(LocalTransaction transaction) {
		return new SlotBasedChangeSetBuilder(transaction);
	}

	public ChangeSetProcessor newProcessorFor(ObjectContainer container) {
		return new SlotBasedChangeSetProcessor(container);
	}

}
