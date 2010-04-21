package com.db4o.internal.activation;

public class TPUpdateDepthProvider implements UpdateDepthProvider {

	public FixedUpdateDepth forDepth(int depth) {
		return new TPFixedUpdateDepth(depth, tpCommit);
	}

	public UnspecifiedUpdateDepth unspecified() {
		// TODO Auto-generated method stub
		return null;
	}

}
