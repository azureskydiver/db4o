/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.operand;

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;


public class MethodCallValue extends ComparisonOperandDescendant {
	private final MethodRef _method;
	private final ComparisonOperand[] _args;
	private final CallingConvention _callingConvention;
	
	public MethodCallValue(MethodRef method, CallingConvention callingConvention, ComparisonOperandAnchor parent, ComparisonOperand[] args) {
		super(parent);
		_method = method;
		_args = args;
		_callingConvention = callingConvention;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @sharpen.property
	 */
	public ComparisonOperand[] args() {
		return _args;
	}
	
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		MethodCallValue casted=(MethodCallValue)obj;
		return _method.equals(casted._method);
	}

	public int hashCode() {
		int hc=super.hashCode();
		hc*=29+_method.hashCode();
		hc*=29+_args.hashCode();
		return hc;
	}
	
	public String toString() {
		
		return super.toString()
			+ "."
			+ _method.name()
			+ Iterators.join(Iterators.iterate(_args), "(", ")", ", ");
	}
	
	/**
	 * @sharpen.property
	 */
	public MethodRef method() {
		return _method;
	}

	/**
	 * @sharpen.property
	 */
	public CallingConvention callingConvention() {
		return _callingConvention;
	}
}
