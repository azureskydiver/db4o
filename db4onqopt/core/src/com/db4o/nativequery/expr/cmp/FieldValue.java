package com.db4o.nativequery.expr.cmp;

import com.db4o.foundation.*;
import com.db4o.nativequery.expr.cmp.field.*;

// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue implements ComparisonOperand {
	private String _fieldName;
	private ComparisonOperand _root;
	private Collection4 _fieldNames = new Collection4();

	private static ComparisonOperand rootFor(int id) {
		switch(id) {
			case 0: return PredicateFieldRoot.INSTANCE;
			case 1: return CandidateFieldRoot.INSTANCE;
			default: throw new RuntimeException();
		}
	}
	
	public FieldValue(int id,String name) {
		this(rootFor(id),name);
	}

	public FieldValue(int id,String[] names) {
		this(rootFor(id),names);
	}

	public FieldValue(int id,Iterator4 names) {
		this(rootFor(id),names);
	}

	public int parentIdx() {
		if(_root==PredicateFieldRoot.INSTANCE) {
			return 0;
		}
		if(_root==CandidateFieldRoot.INSTANCE) {
			return 1;
		}
		throw new RuntimeException();
	}
	
	public FieldValue(ComparisonOperand root,String name) {
		_root=root;
		_fieldName=name;
		_fieldNames.add(name);
	}
	
	public FieldValue(ComparisonOperand root,String[] fieldNames) {
		_root=root;
		_fieldNames.addAll(fieldNames);
	}

	public FieldValue(ComparisonOperand root, Iterator4 fieldNames) {
		_root=root;
		_fieldNames.addAll(fieldNames);
	}

	public FieldValue descend(String fieldName) {
		_fieldNames.add(fieldName);
		return this;
	}

	public Iterator4 fieldNames() {
		return _fieldNames.strictIterator();
	}

	public ComparisonOperand root() {
		return _root;
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
		Iterator4 firstIter=_fieldNames.iterator();
		Iterator4 secondIter=casted._fieldNames.iterator();
		while(firstIter.hasNext()) {
			if(!firstIter.next().equals(secondIter.next())) {
				return false;
			}
		}
		return _root.equals(casted._root);
	}
	
	public int hashCode() {
		// TODO: implement Collection4#hashCode to give by-value semantics
		int hashCode=0;
		Iterator4 firstIter=_fieldNames.iterator();
		while(firstIter.hasNext()) {
			hashCode*=29+firstIter.next().hashCode();
		}
		return hashCode*29+_root.hashCode();
	}
	
	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append(_root);
		for (Iterator4 nameIter = fieldNames(); nameIter.hasNext();) {
			String fieldName = (String) nameIter.next();
			str.append('.');
			str.append(fieldName);
		}
		return str.toString();
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
