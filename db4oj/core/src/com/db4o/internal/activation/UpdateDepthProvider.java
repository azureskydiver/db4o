package com.db4o.internal.activation;

public interface UpdateDepthProvider {

	UnspecifiedUpdateDepth unspecified(boolean tpCommitMode);
	FixedUpdateDepth forDepth(int depth);
	
}
