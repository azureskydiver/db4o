package com.db4o.nativequery.expr.cmp;

public class ArrayAccessValue implements ComparisonOperand {
	private ComparisonOperand _parent;
	private ComparisonOperand _index;
	
	public ArrayAccessValue(ComparisonOperand parent,ComparisonOperand index) {
		super();
		_parent = parent;
		_index = index;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
	
	public ComparisonOperand parent() {
		return _parent;
	}
	
	public ComparisonOperand index() {
		return _index;
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		ArrayAccessValue casted=(ArrayAccessValue)obj;
		return _parent.equals(casted._parent)&&_index.equals(casted._index);
	}
	
	public int hashCode() {
		return _parent.hashCode()*29+_index.hashCode();
	}
	
	public String toString() {
		return _parent+"["+_index+"]";
	}
}
