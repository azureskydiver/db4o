/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.instrumentation;

import java.lang.reflect.*;

import com.db4o.instrumentation.api.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.nativequery.optimization.*;

class ComparisonBytecodeGeneratingVisitor implements ComparisonOperandVisitor {
	private MethodBuilder _methodBuilder;
	private Class _predicateClass;
	private boolean _inArithmetic=false;
	private Class _opClass=null;
	private Class _staticRoot=null;
	private TypeLoader _typeLoader;

	public ComparisonBytecodeGeneratingVisitor(TypeLoader typeLoader, MethodBuilder methodBuilder, Class predicateClass) {
		this._typeLoader = typeLoader;
		this._methodBuilder = methodBuilder;
		this._predicateClass = predicateClass;
	}

	public void visit(ConstValue operand) {
		Object value = operand.value();
		if(value!=null) {
			_opClass=value.getClass();
		}
		_methodBuilder.ldc(value);
		if(value!=null) {
			box(value.getClass(),!_inArithmetic);
		}
	}	

	public void visit(FieldValue fieldValue) {
		Class lastFieldClass = deduceFieldClass(fieldValue);
		Class parentClass=deduceFieldClass(fieldValue.parent());
		boolean needConversion=lastFieldClass.isPrimitive();
			
		fieldValue.parent().accept(this);
		if(_staticRoot!=null) {
			_methodBuilder.loadStaticField(fieldReference(_staticRoot, lastFieldClass,fieldValue.fieldName()));
			_staticRoot=null;
			return;
		}
		FieldRef fieldRef=fieldReference(parentClass,lastFieldClass,fieldValue.fieldName());
		_methodBuilder.loadField(fieldRef);
		
		box(lastFieldClass,!_inArithmetic&&needConversion);
	}

	public void visit(CandidateFieldRoot root) {
		_methodBuilder.loadArgument(1);
	}

	public void visit(PredicateFieldRoot root) {
		_methodBuilder.loadArgument(0);
	}

	public void visit(StaticFieldRoot root) {
		_staticRoot=_typeLoader.loadType(root.className());
	}

	public void visit(ArrayAccessValue operand) {
		Class cmpType=deduceFieldClass(operand.parent()).getComponentType();
		operand.parent().accept(this);
		boolean outerInArithmetic=_inArithmetic;
		_inArithmetic=true;
		operand.index().accept(this);
		_inArithmetic=outerInArithmetic;
		_methodBuilder.loadArrayElement(cmpType);
		box(cmpType, !_inArithmetic);
	}

	public void visit(MethodCallValue operand) {
		Class rcvType=deduceFieldClass(operand.parent());
		Method method=ReflectUtil.methodFor(rcvType, operand.methodName(), operand.paramTypes());
		Class retType=method.getReturnType();
		// FIXME: this should be handled within conversions
		boolean needConversion=retType.isPrimitive();
		operand.parent().accept(this);
		boolean oldInArithmetic=_inArithmetic;
		for (int paramIdx = 0; paramIdx < operand.args().length; paramIdx++) {
			_inArithmetic=operand.paramTypes()[paramIdx].isPrimitive();
			operand.args()[paramIdx].accept(this);
		}
		_inArithmetic=oldInArithmetic;
		_methodBuilder.invoke(method);
		box(retType, !_inArithmetic&&needConversion);
	}

	public void visit(ArithmeticExpression operand) {
		boolean oldInArithmetic=_inArithmetic;
		_inArithmetic=true;
		operand.left().accept(this);
		operand.right().accept(this);
		Class operandType=arithmeticType(operand);
		switch(operand.op().id()) {
			case ArithmeticOperator.ADD_ID:
				_methodBuilder.add(operandType);
				break;
			case ArithmeticOperator.SUBTRACT_ID:
				_methodBuilder.subtract(operandType);
				break;
			case ArithmeticOperator.MULTIPLY_ID:
				_methodBuilder.multiply(operandType);
				break;
			case ArithmeticOperator.DIVIDE_ID:
				_methodBuilder.divide(operandType);
				break;
			default:
				throw new RuntimeException("Unknown operand: "+operand.op());
		}
		box(_opClass,!oldInArithmetic);
		_inArithmetic=oldInArithmetic;
		// FIXME: need to map dX,fX,...
	}

	private void box(Class boxedType, boolean canApply) {
		if (!canApply) {
			return;
		}
		_methodBuilder.box(boxedType);
	}

	private Class deduceFieldClass(ComparisonOperand fieldValue) {
		TypeDeducingVisitor visitor=new TypeDeducingVisitor(_predicateClass, _typeLoader);
		fieldValue.accept(visitor);
		return visitor.operandClass();
	}

	private FieldRef fieldReference(Class parentClass, Class fieldClass, String name) {
		return _methodBuilder.references().forField(parentClass, fieldClass, name);
	}


	private Class arithmeticType(ComparisonOperand operand) {
		if (operand instanceof ConstValue) {
			return ((ConstValue) operand).value().getClass();
		}
		if (operand instanceof FieldValue) {
			try {
				return deduceFieldClass(operand);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		if (operand instanceof ArithmeticExpression) {
			ArithmeticExpression expr=(ArithmeticExpression)operand;
			Class left=arithmeticType(expr.left());
			Class right=arithmeticType(expr.right());
			if(left==Double.class||right==Double.class) {
				return Double.class;
			}
			if(left==Float.class||right==Float.class) {
				return Float.class;
			}
			if(left==Long.class||right==Long.class) {
				return Long.class;
			}
			return Integer.class;
		}
		return null;
	}
}
