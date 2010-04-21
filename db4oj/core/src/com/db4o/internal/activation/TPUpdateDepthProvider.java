package com.db4o.internal.activation;

public class TPUpdateDepthProvider implements UpdateDepthProvider {

	public FixedUpdateDepth forDepth(int depth) {
		return new TPFixedUpdateDepth(depth, false);
	}

	public UnspecifiedUpdateDepth unspecified(boolean tpCommitMode) {
		return new TPUnspecifiedUpdateDepth(tpCommitMode);
	}

}
