package com.db4o.internal.activation;

import com.db4o.internal.*;
import com.db4o.ta.*;

public class TPFixedUpdateDepth extends FixedUpdateDepth {

	private boolean _tpCommit;
	
	public TPFixedUpdateDepth(int depth, boolean tpCommit) {
		super(depth);
		_tpCommit = tpCommit;
	}

	void tpCommit(boolean tpCommit) {
		_tpCommit = tpCommit;
	}
	
	public boolean canSkip(ClassMetadata clazz) {
		if(_tpCommit) {
			return false;
		}
		return clazz.reflector().forClass(Activatable.class).isAssignableFrom(clazz.classReflector());
	}

	@Override
	protected FixedUpdateDepth forDepth(int depth) {
		return new TPFixedUpdateDepth(depth, _tpCommit);
	}

}
