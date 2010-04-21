package com.db4o.internal.activation;

public interface UpdateDepthProvider {

	UnspecifiedUpdateDepth unspecified();
	FixedUpdateDepth forDepth(int depth);
	
}
