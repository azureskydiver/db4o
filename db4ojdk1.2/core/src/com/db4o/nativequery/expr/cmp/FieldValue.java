package com.db4o.nativequery.expr.cmp;

import java.util.*;

import com.db4o.nativequery.expr.cmp.ArithmeticExpression.Visitor;
import com.db4o.nativequery.expr.cmp.ComparisonOperand.ComparisonOperandVisitor;

// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue implements ComparisonOperand {
	public interface Visitor extends ComparisonOperandVisitor {

		void visit(FieldValue operand);

	}

	private int _parentIdx;
	private List _fieldNames;

	public FieldValue(int parentIdx,String name) {
		this._parentIdx=parentIdx;
		_fieldNames = new ArrayList();
		descend(name);
	}

	public FieldValue(int parentIdx,List fieldNames) {
		this._parentIdx=parentIdx;
		this._fieldNames = new ArrayList(fieldNames);
	}

	public FieldValue(int parentIdx,String[] fieldNames) {
		this._parentIdx=parentIdx;
		this._fieldNames=new ArrayList();
		for (int fieldIdx = 0; fieldIdx < fieldNames.length; fieldIdx++) {
			descend(fieldNames[fieldIdx]);
		}
	}

	public FieldValue(int parentIdx,Iterator nameIter) {
		this._parentIdx=parentIdx;
		_fieldNames = new ArrayList();
		while(nameIter.hasNext()) {
			descend((String)nameIter.next());
		}
	}

	public FieldValue descend(String fieldName) {
		_fieldNames.add(fieldName);
		return this;
	}

	public Iterator fieldNames() {
		return _fieldNames.iterator();
	}

	public int parentIdx() {
		return _parentIdx;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		FieldValue casted = (FieldValue) other;
		return _fieldNames.equals(casted._fieldNames)&&_parentIdx==casted._parentIdx;
	}
	
	public int hashCode() {
		return _fieldNames.hashCode()*29+_parentIdx;
	}
	
	public String toString() {
		StringBuilder str=new StringBuilder();
		str.append(_parentIdx);
		for (Iterator nameIter = _fieldNames.iterator(); nameIter.hasNext();) {
			String fieldName = (String) nameIter.next();
			str.append('.');
			str.append(fieldName);
		}
		return str.toString();
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		((Visitor)visitor).visit(this);
	}
}
