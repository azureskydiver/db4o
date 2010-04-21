/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.internal.activation;

import com.db4o.internal.*;

public class UnspecifiedUpdateDepth implements UpdateDepth {

	public final static UnspecifiedUpdateDepth INSTANCE = new UnspecifiedUpdateDepth(false);
	public final static UnspecifiedUpdateDepth TP_INSTANCE = new UnspecifiedUpdateDepth(true);
	
	private boolean _tpMode;
	
	private UnspecifiedUpdateDepth(boolean tpMode) {
		_tpMode = tpMode;
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
        FixedUpdateDepth depth = (FixedUpdateDepth) clazz.updateDepthFromConfig().descend();
        depth.tpMode(_tpMode);
		return depth;
	}

	public UpdateDepth adjustUpdateDepthForCascade(boolean isCollection) {
		throw new IllegalStateException();
	}

	public UpdateDepth descend() {
		throw new IllegalStateException();
	}
	
	public boolean tpMode() {
		return _tpMode;
	}
}
