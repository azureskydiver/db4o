/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.internal.activation;

import com.db4o.internal.*;

public class UpdateDepthFactory {

	public static UpdateDepth forDepth(int depth) {
		if(depth == Const4.UNSPECIFIED) {
			return UnspecifiedUpdateDepth.INSTANCE;
		}
		return new FixedUpdateDepth(depth);
	}
	
	private UpdateDepthFactory() {
	}
}
