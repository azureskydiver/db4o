package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class StaticFieldRoot implements FieldRoot {
	private String _className;
	
	public StaticFieldRoot(String className) {
		this._className = className;
	}

	public String className() {
		return _className;
	}

	public void accept(FieldRootVisitor visitor) {
		visitor.visit(this);
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
}
