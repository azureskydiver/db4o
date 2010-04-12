/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.internal.activation;

import com.db4o.internal.*;

public class UnspecifiedUpdateDepth implements UpdateDepth {

	public final static UnspecifiedUpdateDepth INSTANCE = new UnspecifiedUpdateDepth();
	
	private UnspecifiedUpdateDepth() {
	}

	public boolean sufficientDepth() {
		return true;
	}
	
	public boolean negative() {
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	public UpdateDepth adjust(ClassMetadata clazz) {
        FixedUpdateDepth depth = clazz.updateDepthFromConfig();
//        depth = clazz.adjustCollectionDepthToBorders(depth);
//        return depth.adjust(clazz);
        return depth.descend();
	}

	public UpdateDepth adjustUpdateDepthForCascade(boolean isCollection) {
		throw new IllegalStateException();
	}

	public UpdateDepth descend() {
		throw new IllegalStateException();
	}
}
