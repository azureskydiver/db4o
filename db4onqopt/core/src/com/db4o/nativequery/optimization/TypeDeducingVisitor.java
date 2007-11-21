/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

/**
 * 
 */
package com.db4o.nativequery.optimization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import EDU.purdue.cs.bloat.file.ClassSource;

import com.db4o.nativequery.expr.cmp.operand.*;

class TypeDeducingVisitor implements ComparisonOperandVisitor {
	private Class _predicateClass;
	private Class _candidateClass;
	private Class _clazz;
	private ClassSource _classSource;
	
	public TypeDeducingVisitor(Class predicateClass, Class candidateClass,ClassSource classSource) {
		this._predicateClass = predicateClass;
		this._candidateClass = candidateClass;
		this._classSource = classSource;
		_clazz=null;
	}

	public void visit(PredicateFieldRoot root) {
		_clazz=_predicateClass;
	}

	public void visit(CandidateFieldRoot root) {
		_clazz=_candidateClass;
	}

	public void visit(StaticFieldRoot root) {
		try {
			_clazz=_classSource.loadClass(root.className());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Class operandClass() {
		return _clazz;
	}

	public void visit(ArithmeticExpression operand) {
	}

	public void visit(ConstValue operand) {
		_clazz=operand.value().getClass();
	}

	public void visit(FieldValue operand) {
		operand.parent().accept(this);
		try {
			_clazz=fieldFor(_clazz,operand.fieldName()).getType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void visit(ArrayAccessValue operand) {
		operand.parent().accept(this);
		_clazz=_clazz.getComponentType();
	}
	
	Field fieldFor(Class clazz,String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
		}
		return null;
	}

	public void visit(MethodCallValue operand) {
		operand.parent().accept(this);
		Method method=ReflectUtil.methodFor(_clazz, operand.methodName(), operand.paramTypes());
		_clazz=method.getReturnType();
	}
}