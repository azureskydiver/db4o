package com.db4o.internal.activation;

public class LegacyUpdateDepthProvider implements UpdateDepthProvider {

	public FixedUpdateDepth forDepth(int depth) {
		return new LegacyFixedUpdateDepth(depth);
	}

	public UnspecifiedUpdateDepth unspecified(boolean tpCommitMode) {
		return LegacyUnspecifiedUpdateDepth.INSTANCE;
	}

}
