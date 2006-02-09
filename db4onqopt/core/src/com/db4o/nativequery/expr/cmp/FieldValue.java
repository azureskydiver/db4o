package com.db4o.nativequery.expr.cmp;

import com.db4o.nativequery.expr.cmp.field.*;

// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue implements ComparisonOperand {
	private String _fieldName;
	private ComparisonOperand _parent;
	
	public FieldValue(ComparisonOperand root,String name) {
		_parent=root;
		_fieldName=name;
	}

	// TODO delete
	public int parentIdx() {
		if(_parent==PredicateFieldRoot.INSTANCE) {
			return 0;
		}
		if(_parent==CandidateFieldRoot.INSTANCE) {
			return 1;
		}
		throw new RuntimeException();
	}

	public String fieldName() {
		return _fieldName;
	}
	
	public ComparisonOperand root() {
		if(_parent instanceof FieldValue) {
			return ((FieldValue)_parent).root();
		}
		return _parent;
	}

	public ComparisonOperand parent() {
		return _parent;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		FieldValue casted = (FieldValue) other;
		return _parent.equals(casted._parent)&&_fieldName.equals(casted._fieldName);
	}
	
	public int hashCode() {
		return _parent.hashCode()*29+_fieldName.hashCode();
	}
	
	public String toString() {
		return _parent+"."+_fieldName;
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
