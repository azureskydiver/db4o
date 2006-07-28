/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp;

public abstract class ComparisonOperandDescendant implements ComparisonOperandAnchor {
	private ComparisonOperandAnchor _parent;
	
	protected ComparisonOperandDescendant(ComparisonOperandAnchor _parent) {
		this._parent = _parent;
	}

	public final ComparisonOperandAnchor parent() {
		return _parent;
	}
	
	public final ComparisonOperandAnchor root() {
		return _parent.root();
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		ComparisonOperandDescendant casted=(ComparisonOperandDescendant)obj;
		return _parent.equals(casted._parent);
	}
	
	public int hashCode() {
		return _parent.hashCode();
	}
	
	public String toString() {
		return _parent.toString();
	}
}
