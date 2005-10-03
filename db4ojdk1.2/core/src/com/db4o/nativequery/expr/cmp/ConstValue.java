package com.db4o.nativequery.expr.cmp;


public class ConstValue implements ComparisonOperand {	
	public interface Visitor extends ComparisonOperandVisitor {

		void visit(ConstValue operand);

	}

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
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		ConstValue casted = (ConstValue) other;
		return _value.equals(casted._value);
	}
	
	public int hashCode() {
		return _value.hashCode();
	}

	public void accept(ComparisonOperandVisitor visitor) {
		((Visitor)visitor).visit(this);
	}
}
