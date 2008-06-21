package com.db4o.ibs;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * Ties together a {@link ChangeSetBuilder} and its respective {@link ChangeSetProcessor}.
 */
public interface ChangeSetEngine {
	
	ChangeSetBuilder newBuilderFor(LocalTransaction transaction);
	
	ChangeSetProcessor newProcessorFor(ObjectContainer container);
}
