/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.cobra.qlin;

import com.db4o.query.*;

public class CLinConstraint<T> extends CLinSubNode<T>{
	
	private final Constraint _constraint;
	
	public CLinConstraint(CLinRoot<T> root, Constraint constraint) {
		super(root);
		_constraint = constraint;
	}
	
}
