/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import java.lang.reflect.*;

import com.db4o.instrumentation.api.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;

class ComparisonBytecodeGeneratingVisitor implements ComparisonOperandVisitor {
	private MethodBuilder methodEditor;
	private Class predicateClass;
	private boolean inArithmetic=false;
	private Class opClass=null;
	private Class staticRoot=null;
	private TypeLoader typeLoader;

	public ComparisonBytecodeGeneratingVisitor(TypeLoader typeLoader, MethodBuilder methodEditor, Class predicateClass) {
		this.typeLoader = typeLoader;
		this.methodEditor = methodEditor;
		this.predicateClass = predicateClass;
	}

	public void visit(ConstValue operand) {
		Object value = operand.value();
		if(value!=null) {
			opClass=value.getClass();
		}
		methodEditor.ldc(value);
		if(value!=null) {
			applyConversion(value.getClass(),!inArithmetic);
		}
	}	

	public void visit(FieldValue fieldValue) {
		try {
			Class lastFieldClass = deduceFieldClass(fieldValue);
			Class parentClass=deduceFieldClass(fieldValue.parent());
			boolean needConversion=lastFieldClass.isPrimitive();
			
			fieldValue.parent().accept(this);
			if(staticRoot!=null) {
				methodEditor.loadStaticField(createFieldReference(staticRoot, lastFieldClass,fieldValue.fieldName()));
				staticRoot=null;
				return;
			}
			FieldRef fieldRef=createFieldReference(parentClass,lastFieldClass,fieldValue.fieldName());
			methodEditor.loadField(fieldRef);
			
			applyConversion(lastFieldClass,!inArithmetic&&needConversion);
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}

	public void visit(CandidateFieldRoot root) {
		methodEditor.loadArgument(1);
	}

	public void visit(PredicateFieldRoot root) {
		methodEditor.loadArgument(0);
	}

	public void visit(StaticFieldRoot root) {
		try {
			staticRoot=typeLoader.loadType(root.className());
		} catch (InstrumentationException e) {
			e.printStackTrace();
		}
	}

	public void visit(ArrayAccessValue operand) {
		Class cmpType=deduceFieldClass(operand.parent()).getComponentType();
		operand.parent().accept(this);
		boolean outerInArithmetic=inArithmetic;
		inArithmetic=true;
		operand.index().accept(this);
		inArithmetic=outerInArithmetic;
		methodEditor.loadArrayElement(cmpType);
		applyConversion(cmpType, !inArithmetic);
	}

	public void visit(MethodCallValue operand) {
		Class rcvType=deduceFieldClass(operand.parent());
		Method method=ReflectUtil.methodFor(rcvType, operand.methodName(), operand.paramTypes());
		Class retType=method.getReturnType();
		// FIXME: this should be handled within conversions
		boolean needConversion=retType.isPrimitive();
		operand.parent().accept(this);
		boolean oldInArithmetic=inArithmetic;
		for (int paramIdx = 0; paramIdx < operand.args().length; paramIdx++) {
			inArithmetic=operand.paramTypes()[paramIdx].isPrimitive();
			operand.args()[paramIdx].accept(this);
		}
		inArithmetic=oldInArithmetic;
		methodEditor.invoke(method);
		applyConversion(retType, !inArithmetic&&needConversion);
	}

	public void visit(ArithmeticExpression operand) {
		boolean oldInArithmetic=inArithmetic;
		inArithmetic=true;
		operand.left().accept(this);
		operand.right().accept(this);
		Class operandType=arithmeticType(operand);
		switch(operand.op().id()) {
			case ArithmeticOperator.ADD_ID:
				methodEditor.add(operandType);
				break;
			case ArithmeticOperator.SUBTRACT_ID:
				methodEditor.subtract(operandType);
				break;
			case ArithmeticOperator.MULTIPLY_ID:
				methodEditor.multiply(operandType);
				break;
			case ArithmeticOperator.DIVIDE_ID:
				methodEditor.divide(operandType);
				break;
			default:
				throw new RuntimeException("Unknown operand: "+operand.op());
		}
		applyConversion(opClass,!oldInArithmetic);
		inArithmetic=oldInArithmetic;
		// FIXME: need to map dX,fX,...
	}

	private void applyConversion(Class boxedType, boolean canApply) {
		if (!canApply) {
			return;
		}
		methodEditor.box(boxedType);
	}

	private Class deduceFieldClass(ComparisonOperand fieldValue) {
		TypeDeducingVisitor visitor=new TypeDeducingVisitor(predicateClass, typeLoader);
		fieldValue.accept(visitor);
		return visitor.operandClass();
	}

	private FieldRef createFieldReference(Class parentClass,Class fieldClass,String name) throws NoSuchFieldException {
		return methodEditor.references().forField(parentClass, fieldClass, name);
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
