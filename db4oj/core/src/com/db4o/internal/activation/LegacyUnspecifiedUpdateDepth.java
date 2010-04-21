package com.db4o.internal.activation;

import com.db4o.internal.*;

public class LegacyUnspecifiedUpdateDepth extends UnspecifiedUpdateDepth {

	public final static LegacyUnspecifiedUpdateDepth INSTANCE = new LegacyUnspecifiedUpdateDepth();
	
	private LegacyUnspecifiedUpdateDepth() {
	}

	public boolean canSkip(ClassMetadata clazz) {
		return false;
	}

}
