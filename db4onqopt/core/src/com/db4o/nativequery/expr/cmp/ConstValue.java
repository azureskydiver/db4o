package com.db4o.nativequery.expr.cmp;


public class ConstValue implements ComparisonOperand {	
	private Object _value;
	
	public ConstValue(Object value) {
		this._value=value;
	}
	
	public Object value() {
		return _value;
	}
	
	public String toString() {
		return (_value==null ? "[null]" : _value.toString());
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other==null || getClass() != other.getClass()) {
			return false;
		}
		Object otherValue = ((ConstValue) other)._value;
		if (otherValue == _value) {
			return true;
		}
		if (otherValue == null || _value == null) {
			return false;
		}
		return _value.equals(otherValue);
	}
	
	public int hashCode() {
		return _value.hashCode();
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
