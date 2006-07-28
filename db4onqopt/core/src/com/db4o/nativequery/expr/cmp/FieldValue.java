/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp;


public class FieldValue extends ComparisonOperandDescendant {
	
	private String _fieldName;
	
	private Object _tag;
	
	public FieldValue(ComparisonOperandAnchor root, String name) {
		this(root, name, null);
	}
	
	public FieldValue(ComparisonOperandAnchor root, String name, Object tag) {
		super(root);
		_fieldName=name;
		_tag=tag;
	}

	public String fieldName() {
		return _fieldName;
	}
	
	public boolean equals(Object other) {
		if(!super.equals(other)) {
			return false;
		}
		FieldValue casted = (FieldValue) other;
		if(_tag==null) {
			if(casted._tag!=null) {
				return false;
			}
		}
		else {
			if(!_tag.equals(casted._tag)) {
				return false;
			}
		}
		return _fieldName.equals(casted._fieldName);
	}
	
	public int hashCode() {
		int hash=super.hashCode()*29+_fieldName.hashCode();
		if(_tag!=null) {
			hash*=29+_tag.hashCode();
		}
		return hash;
	}
	
	public String toString() {
		return super.toString()+"."+_fieldName;
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * Code analysis specific information.
	 * 
	 * This is used in the .net side to preserve Mono.Cecil references
	 * for instance.
	 */
	public Object tag() {
		return _tag;
	}
	
	public void tag(Object value) {
		_tag = value;
	}
}
