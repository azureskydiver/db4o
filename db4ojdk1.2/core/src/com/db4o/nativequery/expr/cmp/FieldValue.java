package com.db4o.nativequery.expr.cmp;

import com.db4o.foundation.*;

// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue implements ComparisonOperand {
	public interface Visitor extends ComparisonOperandVisitor {

		void visit(FieldValue operand);

	}

	private int _parentIdx;
	private Collection4 _fieldNames = new Collection4();

	public FieldValue(int parentIdx,String name) {
		this._parentIdx=parentIdx;
		descend(name);
	}
	
	public FieldValue(int parentIdx,String[] fieldNames) {
		this._parentIdx=parentIdx;
		_fieldNames.addAll(fieldNames);
	}

	public FieldValue(int parentIdx, Iterator4 fieldNames) {
		this._parentIdx=parentIdx;
		_fieldNames.addAll(fieldNames);
	}

	public FieldValue descend(String fieldName) {
		_fieldNames.add(fieldName);
		return this;
	}

	public Iterator4 fieldNames() {
		return _fieldNames.strictIterator();
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
		
		// TODO: implement Collection4#equals to give by-value semantics
		if(_fieldNames.size()!=casted._fieldNames.size()) {
			return false;
		}
		Iterator4 firstIter=_fieldNames.fastIterator();
		Iterator4 secondIter=casted._fieldNames.fastIterator();
		while(firstIter.hasNext()) {
			if(!firstIter.next().equals(secondIter.next())) {
				return false;
			}
		}
		return _parentIdx==casted._parentIdx;
	}
	
	public int hashCode() {
		// TODO: implement Collection4#hashCode to give by-value semantics
		int hashCode=0;
		Iterator4 firstIter=_fieldNames.fastIterator();
		while(firstIter.hasNext()) {
			hashCode*=29+firstIter.next().hashCode();
		}
		return hashCode*29+_parentIdx;
	}
	
	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append(_parentIdx);
		for (Iterator4 nameIter = fieldNames(); nameIter.hasNext();) {
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
