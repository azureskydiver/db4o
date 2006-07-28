/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class StaticFieldRoot extends ComparisonOperandRoot {
	private String _className;
	
	public StaticFieldRoot(String className) {
		this._className = className;
	}

	public String className() {
		return _className;
	}

	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		StaticFieldRoot casted=(StaticFieldRoot)obj;
		return _className.equals(casted._className);
	}
	
	public int hashCode() {
		return _className.hashCode();
	}
	
	public String toString() {
		return _className;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
