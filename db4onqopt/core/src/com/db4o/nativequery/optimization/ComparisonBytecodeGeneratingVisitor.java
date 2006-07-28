/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import java.lang.reflect.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;

import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;

class ComparisonBytecodeGeneratingVisitor implements ComparisonOperandVisitor {
	private MethodEditor methodEditor;
	private Class predicateClass;
	private Class candidateClass;
	
	private Map conversions;
	private boolean inArithmetic=false;
	private Class opClass=null;
	private Class staticRoot=null;

	public ComparisonBytecodeGeneratingVisitor(MethodEditor methodEditor,Class predicateClass,Class candidateClass) {
		this.methodEditor = methodEditor;
		this.predicateClass=predicateClass;
		this.candidateClass=candidateClass;
		buildConversions();
	}

	public void visit(ConstValue operand) {
		Object value = operand.value();
		if(value!=null) {
			opClass=value.getClass();
			prepareConversion(value.getClass(),!inArithmetic);
		}
		methodEditor.addInstruction(Opcode.opc_ldc,value);
		if(value!=null) {
			applyConversion(value.getClass(),!inArithmetic);
		}
		// FIXME handle char, boolean,...
	}

	public void visit(FieldValue fieldValue) {
		try {
			Class lastFieldClass = deduceFieldClass(fieldValue);
			Class parentClass=deduceFieldClass(fieldValue.parent());
			boolean needConversion=lastFieldClass.isPrimitive();
			prepareConversion(lastFieldClass,!inArithmetic&&needConversion);
			
			fieldValue.parent().accept(this);
			if(staticRoot!=null) {
				methodEditor.addInstruction(Opcode.opc_getstatic,createFieldReference(staticRoot, lastFieldClass,fieldValue.fieldName()));
				staticRoot=null;
				return;
			}
			MemberRef fieldRef=createFieldReference(parentClass,lastFieldClass,fieldValue.fieldName());
			methodEditor.addInstruction(Opcode.opc_getfield,fieldRef);
			
			applyConversion(lastFieldClass,!inArithmetic&&needConversion);
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}

	public void visit(CandidateFieldRoot root) {
		methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable(1));
	}

	public void visit(PredicateFieldRoot root) {
		methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable(0));
	}

	public void visit(StaticFieldRoot root) {
		try {
			staticRoot=Class.forName(root.className());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void visit(ArrayAccessValue operand) {
		Class cmpType=deduceFieldClass(operand.parent()).getComponentType();
		prepareConversion(cmpType, !inArithmetic);
		operand.parent().accept(this);
		boolean outerInArithmetic=inArithmetic;
		inArithmetic=true;
		operand.index().accept(this);
		inArithmetic=outerInArithmetic;
		int opcode=Opcode.opc_aaload;
		if(cmpType==Integer.TYPE) {
			opcode=Opcode.opc_iaload;
		}
		if(cmpType==Long.TYPE) {
			opcode=Opcode.opc_laload;
		}
		if(cmpType==Float.TYPE) {
			opcode=Opcode.opc_faload;
		}
		if(cmpType==Double.TYPE) {
			opcode=Opcode.opc_daload;
		}
		methodEditor.addInstruction(opcode);
		applyConversion(cmpType, !inArithmetic);
	}

	public void visit(MethodCallValue operand) {
		Class rcvType=deduceFieldClass(operand.parent());
		Method method=ReflectUtil.methodFor(rcvType, operand.methodName(), operand.paramTypes());
		Class retType=method.getReturnType();
		// FIXME: this should be handled within conversions
		boolean needConversion=retType.isPrimitive();
		prepareConversion(retType, !inArithmetic&&needConversion);
		operand.parent().accept(this);
		boolean oldInArithmetic=inArithmetic;
		for (int paramIdx = 0; paramIdx < operand.args().length; paramIdx++) {
			inArithmetic=operand.paramTypes()[paramIdx].isPrimitive();
			operand.args()[paramIdx].accept(this);
		}
		inArithmetic=oldInArithmetic;
		// FIXME: invokeinterface
		int opcode=((method.getModifiers()&Modifier.STATIC)!=0 ? Opcode.opc_invokestatic : Opcode.opc_invokevirtual);
		methodEditor.addInstruction(opcode,createMethodReference(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), method.getReturnType()));
		applyConversion(retType, !inArithmetic&&needConversion);
	}

	public void visit(ArithmeticExpression operand) {
		boolean oldInArithmetic=inArithmetic;
		inArithmetic=true;
		Instruction newInstr=prepareConversion(opClass,!oldInArithmetic,true);
		operand.left().accept(this);
		operand.right().accept(this);
		Class operandType=arithmeticType(operand);
		int opcode=Integer.MIN_VALUE;
		switch(operand.op().id()) {
			case ArithmeticOperator.ADD_ID:
				if(operandType==Double.class) {
					opcode=Opcode.opc_dadd;
					break;
				}
				if(operandType==Float.class) {
					opcode=Opcode.opc_fadd;
					break;
				}
				if(operandType==Long.class) {
					opcode=Opcode.opc_ladd;
					break;
				}
				opcode=Opcode.opc_iadd;
				break;
			case ArithmeticOperator.SUBTRACT_ID:
				if(operandType==Double.class) {
					opcode=Opcode.opc_dsub;
					break;
				}
				if(operandType==Float.class) {
					opcode=Opcode.opc_fsub;
					break;
				}
				if(operandType==Long.class) {
					opcode=Opcode.opc_lsub;
					break;
				}
				opcode=Opcode.opc_isub;
				break;
			case ArithmeticOperator.MULTIPLY_ID:
				if(operandType==Double.class) {
					opcode=Opcode.opc_dmul;
					break;
				}
				if(operandType==Float.class) {
					opcode=Opcode.opc_fmul;
					break;
				}
				if(operandType==Long.class) {
					opcode=Opcode.opc_lmul;
					break;
				}
				opcode=Opcode.opc_imul;
				break;
			case ArithmeticOperator.DIVIDE_ID:
				if(operandType==Double.class) {
					opcode=Opcode.opc_ddiv;
					break;
				}
				if(operandType==Float.class) {
					opcode=Opcode.opc_fdiv;
					break;
				}
				if(operandType==Long.class) {
					opcode=Opcode.opc_ldiv;
					break;
				}
				opcode=Opcode.opc_idiv;
				break;
			default:
				throw new RuntimeException("Unknown operand: "+operand.op());
		}
		methodEditor.addInstruction(opcode);
		if(newInstr!=null) {
			newInstr.setOperand(createType(opClass));
		}
		applyConversion(opClass,!oldInArithmetic);
		inArithmetic=oldInArithmetic;
		// FIXME: need to map dX,fX,...
	}

	private Class deduceFieldClass(ComparisonOperand fieldValue) {
		TypeDeducingVisitor visitor=new TypeDeducingVisitor(predicateClass,candidateClass);
		fieldValue.accept(visitor);
		return visitor.operandClass();
	}

	private MemberRef createFieldReference(Class parentClass,Class fieldClass,String name) throws NoSuchFieldException {
		NameAndType nameAndType=new NameAndType(name,createType(fieldClass));
		return new MemberRef(createType(parentClass),nameAndType);
	}


	private Class arithmeticType(ComparisonOperand operand) {
		if (operand instanceof ConstValue) {
			return ((ConstValue) operand).value().getClass();
		}
		if (operand instanceof FieldValue) {
			try {
				return deduceFieldClass((FieldValue) operand);
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

	private Instruction prepareConversion(Class clazz,boolean canApply) {
		return prepareConversion(clazz,canApply,false);
	}

	private Instruction prepareConversion(Class clazz,boolean canApply,boolean force) {
		if((force||conversions.containsKey(clazz))&&canApply) {
			Class[] convSpec=(Class[])conversions.get(clazz);
			Instruction newInstruction=new Instruction(Opcode.opc_new,(convSpec==null ? null : createType(convSpec[0])));
			methodEditor.addInstruction(newInstruction);
			methodEditor.addInstruction(Opcode.opc_dup);
			return newInstruction;
		}
		return null;
	}

	private void applyConversion(Class clazz,boolean canApply) {
		if(conversions.containsKey(clazz)&&canApply) {
			Class[] convSpec=(Class[])conversions.get(clazz);
			methodEditor.addInstruction(Opcode.opc_invokespecial,createMethodReference(convSpec[0],"<init>",new Class[]{convSpec[1]},Void.TYPE));
		}
	}

	private MemberRef createMethodReference(Class parent,String name,Class[] args,Class ret) {
		Type[] argTypes=new Type[args.length];
		for (int argIdx = 0; argIdx < args.length; argIdx++) {
			argTypes[argIdx]=createType(args[argIdx]);
		}
		NameAndType nameAndType=new NameAndType(name,Type.getType(argTypes,createType(ret)));
		return new MemberRef(createType(parent),nameAndType);
	}
	
	private Type createType(Class clazz) {
		return Type.getType(clazz);
	}
	
	private void buildConversions() {
		conversions=new HashMap();
		conversions.put(Integer.class,new Class[]{Integer.class,Integer.TYPE});
		conversions.put(Long.class,new Class[]{Long.class,Long.TYPE});
		conversions.put(Short.class,new Class[]{Short.class,Short.TYPE});
		conversions.put(Byte.class,new Class[]{Byte.class,Byte.TYPE});
		conversions.put(Double.class,new Class[]{Double.class,Double.TYPE});
		conversions.put(Float.class,new Class[]{Float.class,Float.TYPE});
		// FIXME this must be handled somewhere else -  FieldValue, etc.
		conversions.put(Integer.TYPE,conversions.get(Integer.class));
		conversions.put(Long.TYPE,conversions.get(Long.class));
		conversions.put(Short.TYPE,conversions.get(Short.class));
		conversions.put(Byte.TYPE,conversions.get(Byte.class));
		conversions.put(Double.TYPE,conversions.get(Double.class));
		conversions.put(Float.TYPE,conversions.get(Float.class));
	}
}
