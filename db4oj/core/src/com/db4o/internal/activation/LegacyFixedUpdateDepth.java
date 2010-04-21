package com.db4o.internal.activation;

import com.db4o.internal.*;

public class LegacyFixedUpdateDepth extends FixedUpdateDepth {

	public LegacyFixedUpdateDepth(int depth) {
		super(depth);
	}

	public boolean canSkip(ClassMetadata clazz) {
		return false;
	}

	@Override
	protected FixedUpdateDepth forDepth(int depth) {
		return new LegacyFixedUpdateDepth(depth);
	}

}
