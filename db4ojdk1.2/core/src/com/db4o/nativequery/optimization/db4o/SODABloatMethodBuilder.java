package com.db4o.nativequery.optimization.db4o;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.inside.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.query.*;

public class SODABloatMethodBuilder {	
	private MethodEditor methodEditor;
	
	private MemberRef descendRef;
	private MemberRef constrainRef;
	private MemberRef greaterRef;
	private MemberRef smallerRef;
	private MemberRef notRef;
	private MemberRef andRef;
	private MemberRef orRef;
	private Type queryType;
	private Map conversions;

	private class SODABloatMethodVisitor implements DiscriminatingExpressionVisitor {
		private Class predicateClass;
		
		public SODABloatMethodVisitor(Class predicateClass) {
			this.predicateClass=predicateClass;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,andRef);
		}

		public void visit(BoolConstExpression expression) {
			throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,orRef);
		}

		public void visit(final ComparisonExpression expression) {
			methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable("query",queryType,1));
			Iterator fieldNames = expression.left().fieldNames();
			while(fieldNames.hasNext()) {
				methodEditor.addInstruction(Opcode.opc_ldc,(String)fieldNames.next());
				methodEditor.addInstruction(Opcode.opc_invokeinterface,descendRef);
			}
			expression.right().accept(new DiscriminatingComparisonOperandVisitor() {
				private boolean inArithmetic=false;
				private Class opClass=null;
				
				public void visit(ConstValue operand) {
					Object value = operand.value();
					if(value!=null) {
						opClass=value.getClass();
					}
					methodEditor.addInstruction(Opcode.opc_ldc,value);
					if(value!=null&&conversions.containsKey(value.getClass())&&!inArithmetic) {
						methodEditor.addInstruction(Opcode.opc_invokestatic,conversions.get(value.getClass()));
					}
					// FIXME handle char, boolean,...
				}

				public void visit(FieldValue fieldValue) {
					Iterator targetFieldNames =fieldValue.fieldNames();
					methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable(0));
					try {
						Class curClass=predicateClass;
						while(targetFieldNames.hasNext()) {
							String fieldName=(String)targetFieldNames.next();
							Class fieldClass=fieldClass(curClass,fieldName);
							methodEditor.addInstruction(Opcode.opc_getfield,createFieldReference(curClass,fieldClass,fieldName));
							curClass=fieldClass;
						}
						opClass=curClass;
						if(conversions.containsKey(curClass)&&!inArithmetic) {
							methodEditor.addInstruction(Opcode.opc_invokestatic,conversions.get(curClass));
						}
					} catch (NoSuchFieldException exc) {
						throw new RuntimeException(exc.getMessage());
					}
				}

				public void visit(ArithmeticExpression operand) {
					boolean oldInArithmetic=inArithmetic;
					inArithmetic=true;
					operand.left().accept(this);
					operand.right().accept(this);
					switch(operand.op().id()) {
						case ArithmeticOperator.ADD_ID:
							methodEditor.addInstruction(Opcode.opc_iadd);
							break;
						case ArithmeticOperator.SUBTRACT_ID:
							methodEditor.addInstruction(Opcode.opc_isub);
							break;
						case ArithmeticOperator.MULTIPLY_ID:
							methodEditor.addInstruction(Opcode.opc_imul);
							break;
						case ArithmeticOperator.DIVIDE_ID:
							methodEditor.addInstruction(Opcode.opc_idiv);
							break;
						default:
							throw new RuntimeException("Unknown operand: "+operand.op());
					}
					if(!oldInArithmetic) {
						if(conversions.containsKey(opClass)) {
							methodEditor.addInstruction(Opcode.opc_invokestatic,conversions.get(opClass));
						}
					}
					inArithmetic=oldInArithmetic;
					// FIXME: need to map dX,fX,...
				}
				
			});
			methodEditor.addInstruction(Opcode.opc_invokeinterface,constrainRef);
			if(!expression.op().equals(ComparisonOperator.EQUALS)) {
				if(expression.op().equals(ComparisonOperator.GREATER)) {
					methodEditor.addInstruction(Opcode.opc_invokeinterface,greaterRef);
				}
				else {
					methodEditor.addInstruction(Opcode.opc_invokeinterface,smallerRef);
				}
			}
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,notRef);
		}

	}
	
	public SODABloatMethodBuilder() {
		buildMethodReferences();
	}
	
	public MethodEditor injectOptimization(Expression expr, ClassEditor classEditor,ClassLoader classLoader) {
		classEditor.addInterface(Db4oEnhancedFilter.class);
		methodEditor=new MethodEditor(classEditor,Modifiers.PUBLIC,Void.TYPE,"optimizeQuery",new Class[]{Query.class},new Class[]{});
		methodEditor.addLabel(new Label(0,true));
		try {
			Class predicateClass = classLoader.loadClass(classEditor.name().replace('/','.'));
			expr.accept(new SODABloatMethodVisitor(predicateClass));
			methodEditor.addInstruction(Opcode.opc_pop);
			methodEditor.addLabel(new Label(1,false));
			methodEditor.addInstruction(Opcode.opc_return);
			methodEditor.addLabel(new Label(2,true));
			return methodEditor;
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
	
	private void buildMethodReferences() {
		queryType = Type.getType(Query.class);
		descendRef=createMethodReference(Query.class,"descend",new Class[]{String.class},Query.class);
		constrainRef=createMethodReference(Query.class,"constrain",new Class[]{Object.class},Constraint.class);
		greaterRef=createMethodReference(Constraint.class,"greater",new Class[]{},Constraint.class);
		smallerRef=createMethodReference(Constraint.class,"smaller",new Class[]{},Constraint.class);
		notRef=createMethodReference(Constraint.class,"not",new Class[]{},Constraint.class);
		andRef=createMethodReference(Constraint.class,"and",new Class[]{Constraint.class},Constraint.class);
		orRef=createMethodReference(Constraint.class,"or",new Class[]{Constraint.class},Constraint.class);
		conversions=new HashMap();
		conversions.put(Integer.class,createMethodReference(Integer.class,"valueOf",new Class[]{Integer.TYPE},Integer.class));
		conversions.put(Long.class,createMethodReference(Long.class,"valueOf",new Class[]{Long.TYPE},Long.class));
		conversions.put(Short.class,createMethodReference(Short.class,"valueOf",new Class[]{Short.TYPE},Short.class));
		conversions.put(Byte.class,createMethodReference(Byte.class,"valueOf",new Class[]{Byte.TYPE},Byte.class));
		conversions.put(Double.class,createMethodReference(Double.class,"valueOf",new Class[]{Double.TYPE},Double.class));
		conversions.put(Float.class,createMethodReference(Float.class,"valueOf",new Class[]{Float.TYPE},Float.class));
		// FIXME this must be handled somewhere else -  FieldValue, etc.
		conversions.put(Integer.TYPE,createMethodReference(Integer.class,"valueOf",new Class[]{Integer.TYPE},Integer.class));
		conversions.put(Long.TYPE,createMethodReference(Long.class,"valueOf",new Class[]{Long.TYPE},Long.class));
		conversions.put(Short.TYPE,createMethodReference(Short.class,"valueOf",new Class[]{Short.TYPE},Short.class));
		conversions.put(Byte.TYPE,createMethodReference(Byte.class,"valueOf",new Class[]{Byte.TYPE},Byte.class));
		conversions.put(Double.TYPE,createMethodReference(Double.class,"valueOf",new Class[]{Double.TYPE},Double.class));
		conversions.put(Float.TYPE,createMethodReference(Float.class,"valueOf",new Class[]{Float.TYPE},Float.class));
	}
	
	private MemberRef createMethodReference(Class parent,String name,Class[] args,Class ret) {
		Type[] argTypes=new Type[args.length];
		for (int argIdx = 0; argIdx < args.length; argIdx++) {
			argTypes[argIdx]=Type.getType(args[argIdx]);
		}
		NameAndType nameAndType=new NameAndType(name,Type.getType(argTypes,Type.getType(ret)));
		return new MemberRef(Type.getType(parent),nameAndType);
	}

	private MemberRef createFieldReference(Class parentClass,Class fieldClass,String name) throws NoSuchFieldException {
		NameAndType nameAndType=new NameAndType(name,Type.getType(fieldClass));
		return new MemberRef(Type.getType(parentClass),nameAndType);
	}

	private Class fieldClass(Class parent, String name) throws NoSuchFieldException {
		return parent.getDeclaredField(name).getType();
	}
}
