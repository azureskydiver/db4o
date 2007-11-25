/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.operand;

import com.db4o.instrumentation.api.*;


public class StaticFieldRoot extends ComparisonOperandRoot {
	
	private TypeRef _type;
	
	public StaticFieldRoot(TypeRef type) {
		_type = type;
	}
	
	/**
	 * @sharpen.property
	 */
	public TypeRef type() {
		return _type;
	}

	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		StaticFieldRoot casted=(StaticFieldRoot)obj;
		return _type.equals(casted._type);
	}
	
	public int hashCode() {
		return _type.hashCode();
	}
	
	public String toString() {
		return _type.toString();
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}	
}
