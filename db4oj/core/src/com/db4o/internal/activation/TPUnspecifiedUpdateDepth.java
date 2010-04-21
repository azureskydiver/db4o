package com.db4o.internal.activation;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class TPUnspecifiedUpdateDepth extends UnspecifiedUpdateDepth {

	protected TPUnspecifiedUpdateDepth(boolean tpCommit) {
	}

	public boolean canSkip(ClassMetadata clazz) {
		throw new NotImplementedException();
	}

}
