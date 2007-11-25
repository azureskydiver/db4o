/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.instrumentation;

import com.db4o.instrumentation.api.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;

class ComparisonBytecodeGeneratingVisitor implements ComparisonOperandVisitor {
	private MethodBuilder _methodBuilder;
	private TypeRef _predicateClass;
	private boolean _inArithmetic=false;
	private TypeRef _opClass=null;
	private TypeRef _staticRoot=null;

	public ComparisonBytecodeGeneratingVisitor(MethodBuilder methodBuilder, TypeRef predicateClass) {
		this._methodBuilder = methodBuilder;
		this._predicateClass = predicateClass;
	}

	public void visit(ConstValue operand) {
		Object value = operand.value();
		if(value!=null) {
			_opClass=typeRef(value.getClass());
		}
		_methodBuilder.ldc(value);
		if(value!=null) {
			box(_opClass,!_inArithmetic);
		}
	}	

	private TypeRef typeRef(Class type) {
		return _methodBuilder.references().forType(type);
	}

	public void visit(FieldValue fieldValue) {
		TypeRef lastFieldClass = fieldValue.field().type();
		boolean needConversion=lastFieldClass.isPrimitive();
			
		fieldValue.parent().accept(this);
		if(_staticRoot!=null) {
			_methodBuilder.loadStaticField(fieldValue.field());
			_staticRoot=null;
			return;
		}
		_methodBuilder.loadField(fieldValue.field());
		
		box(lastFieldClass,!_inArithmetic&&needConversion);
	}

	public void visit(CandidateFieldRoot root) {
		_methodBuilder.loadArgument(1);
	}

	public void visit(PredicateFieldRoot root) {
		_methodBuilder.loadArgument(0);
	}

	public void visit(StaticFieldRoot root) {
		_staticRoot=root.type();
	}

	public void visit(ArrayAccessValue operand) {
		TypeRef cmpType=deduceFieldClass(operand.parent()).elementType();
		operand.parent().accept(this);
		boolean outerInArithmetic=_inArithmetic;
		_inArithmetic=true;
		operand.index().accept(this);
		_inArithmetic=outerInArithmetic;
		_methodBuilder.loadArrayElement(cmpType);
		box(cmpType, !_inArithmetic);
	}

	public void visit(MethodCallValue operand) {
		MethodRef method=operand.method();
		TypeRef retType=method.returnType();
		// FIXME: this should be handled within conversions
		boolean needConversion=retType.isPrimitive();
		operand.parent().accept(this);
		boolean oldInArithmetic=_inArithmetic;
		for (int paramIdx = 0; paramIdx < operand.args().length; paramIdx++) {
			_inArithmetic=operand.method().paramTypes()[paramIdx].isPrimitive();
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
		TypeRef operandType=arithmeticType(operand);
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

	private void box(TypeRef boxedType, boolean canApply) {
		if (!canApply) {
			return;
		}
		_methodBuilder.box(boxedType);
	}

	private TypeRef deduceFieldClass(ComparisonOperand fieldValue) {
		TypeDeducingVisitor visitor=new TypeDeducingVisitor(_methodBuilder.references(), _predicateClass);
		fieldValue.accept(visitor);
		return visitor.operandClass();
	}

	private TypeRef arithmeticType(ComparisonOperand operand) {
		if (operand instanceof ConstValue) {
			return typeRef(((ConstValue) operand).value().getClass());
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
			TypeRef left=arithmeticType(expr.left());
			TypeRef right=arithmeticType(expr.right());
			if(left==doubleRef()||right==doubleRef()) {
				return doubleRef();
			}
			if(left==floatRef()||right==floatRef()) {
				return floatRef();
			}
			if(left==longRef()||right==longRef()) {
				return longRef();
			}
			return typeRef(Integer.class);
		}
		return null;
	}

	private TypeRef longRef() {
		return typeRef(Long.class);
	}

	private TypeRef floatRef() {
		return typeRef(Float.class);
	}

	private TypeRef doubleRef() {
		return typeRef(Double.class);
	}
}
