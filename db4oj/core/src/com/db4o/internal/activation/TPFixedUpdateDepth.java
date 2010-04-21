package com.db4o.internal.activation;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class TPFixedUpdateDepth extends FixedUpdateDepth {

	private boolean _tpCommit;
	
	public TPFixedUpdateDepth(int depth, boolean tpCommit) {
		super(depth);
		_tpCommit = tpCommit;
	}

	public boolean canSkip(ClassMetadata clazz) {
		throw new NotImplementedException();
	}

	@Override
	protected FixedUpdateDepth forDepth(int depth) {
		return new TPFixedUpdateDepth(depth, _tpCommit);
	}

}
