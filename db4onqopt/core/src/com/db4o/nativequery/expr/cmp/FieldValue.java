package com.db4o.nativequery.expr.cmp;

import com.db4o.nativequery.expr.cmp.field.*;

// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue extends ComparisonOperandDescendant {
	private String _fieldName;
	
	public FieldValue(ComparisonOperandAnchor root,String name) {
		super(root);
		_fieldName=name;
	}

	public String fieldName() {
		return _fieldName;
	}
	
	public boolean equals(Object other) {
		if(!super.equals(other)) {
			return false;
		}
		FieldValue casted = (FieldValue) other;
		return _fieldName.equals(casted._fieldName);
	}
	
	public int hashCode() {
		return super.hashCode()*29+_fieldName.hashCode();
	}
	
	public String toString() {
		return super.toString()+"."+_fieldName;
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
