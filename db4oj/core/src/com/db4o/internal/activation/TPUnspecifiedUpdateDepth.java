package com.db4o.internal.activation;

import com.db4o.internal.*;
import com.db4o.ta.*;

public class TPUnspecifiedUpdateDepth extends UnspecifiedUpdateDepth {

	private boolean _tpCommit;
	
	TPUnspecifiedUpdateDepth(boolean tpCommit) {
		_tpCommit = tpCommit;
	}

	public boolean canSkip(ClassMetadata clazz) {
		if(_tpCommit) {
			return false;
		}
		return clazz.reflector().forClass(Activatable.class).isAssignableFrom(clazz.classReflector());
	}

	@Override
	protected FixedUpdateDepth wrap(FixedUpdateDepth depth) {
		((TPFixedUpdateDepth)depth).tpCommit(_tpCommit);
		return depth;
	}

}
