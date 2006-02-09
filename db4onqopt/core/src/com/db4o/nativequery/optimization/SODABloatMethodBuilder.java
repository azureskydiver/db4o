package com.db4o.nativequery.optimization;

import java.lang.reflect.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.foundation.*;
import com.db4o.inside.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.field.*;
import com.db4o.query.*;

// TODO split into top level classes and refactor
public class SODABloatMethodBuilder {	
	private MethodEditor methodEditor;
	
	private MemberRef descendRef;
	private MemberRef constrainRef;
	private MemberRef greaterRef;
	private MemberRef smallerRef;
	private MemberRef containsRef;
	private MemberRef startsWithRef;
	private MemberRef endsWithRef;
	private MemberRef notRef;
	private MemberRef andRef;
	private MemberRef orRef;
	private Type queryType;
	private Map conversions;

	private class SODABloatMethodVisitor implements ExpressionVisitor {
		private ClassLoader classLoader;
		private Class predicateClass;
		private Class candidateClass;
		
		public SODABloatMethodVisitor(Class predicateClass, ClassLoader classLoader) {
			this.predicateClass=predicateClass;
			this.classLoader=classLoader;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,andRef);
		}

		public void visit(BoolConstExpression expression) {
			methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable("query",queryType,1));
			//throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,orRef);
		}

		public void visit(final ComparisonExpression expression) {
			methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable("query",queryType,1));
			Iterator4 fieldNames = fieldNames(expression.left());
			while(fieldNames.hasNext()) {
				methodEditor.addInstruction(Opcode.opc_ldc,(String)fieldNames.next());
				methodEditor.addInstruction(Opcode.opc_invokeinterface,descendRef);
			}
			expression.right().accept(new ComparisonOperandVisitor() {
				private boolean inArithmetic=false;
				private Class opClass=null;
				
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
						boolean needConversion=lastFieldClass.isPrimitive();
						if(needConversion) {
							prepareConversion(lastFieldClass,!inArithmetic);
						}
						
						if(fieldValue.root()==PredicateFieldRoot.INSTANCE) {
							methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable(0));
						}
						else if (fieldValue.root()==CandidateFieldRoot.INSTANCE) {
							methodEditor.addInstruction(Opcode.opc_aload,new LocalVariable(1));
						}
						else {
							StaticFieldRoot root=(StaticFieldRoot)fieldValue.root();
							// FIXME handle chaining
							methodEditor.addInstruction(Opcode.opc_getstatic,createFieldReference(Class.forName(root.className()), lastFieldClass, (String)fieldNames(fieldValue).next()));
							return;
						}
						opClass=lastFieldClass;

						Iterator4 targetFieldNames =fieldNames(fieldValue);
						Class curClass=predicateClass;
						while(targetFieldNames.hasNext()) {
							String fieldName=(String)targetFieldNames.next();
							Class fieldClass=fieldClass(curClass,fieldName);
							MemberRef fieldRef=createFieldReference(curClass,fieldClass,fieldName);
							methodEditor.addInstruction(Opcode.opc_getfield,fieldRef);
							curClass=fieldClass;
						}
						
						
						if(needConversion) {
							applyConversion(opClass,!inArithmetic);
						}
					} catch (Exception exc) {
						throw new RuntimeException(exc.getMessage());
					}
				}

//				private Class fieldType(FieldValue fieldValue) throws NoSuchFieldException {
//					Iterator4 targetFieldNames2 =fieldValue.fieldNames();
//					Class curClass2=predicateClass;
//					while(targetFieldNames2.hasNext()) {
//						String fieldName=(String)targetFieldNames2.next();
//						Class fieldClass=fieldClass(curClass2,fieldName);
//						curClass2=fieldClass;
//					}
//					return curClass2;
//				}

				private Class deduceFieldClass(FieldValue fieldValue) throws Exception {
					FieldRootClassNameVisitor visitor=new FieldRootClassNameVisitor(predicateClass,candidateClass);
					fieldValue.root().accept(visitor);
					Class curClass=classLoader.loadClass(visitor.className());
					Iterator4 fieldIter=fieldNames(fieldValue);
					while(fieldIter.hasNext()) {
						String fieldName=(String)fieldIter.next();
						Field curField=curClass.getDeclaredField(fieldName);
						curClass=curField.getType();
					}
					return curClass;
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

				public void visit(CandidateFieldRoot root) {
					// TODO Auto-generated method stub
					
				}

				public void visit(PredicateFieldRoot root) {
					// TODO Auto-generated method stub
					
				}

				public void visit(StaticFieldRoot root) {
					// TODO Auto-generated method stub
					
				}

				public void visit(ArrayAccessValue operand) {
					// FIXME Auto-generated method stub
					
				}
			});
			methodEditor.addInstruction(Opcode.opc_invokeinterface,constrainRef);
			if(!expression.op().equals(ComparisonOperator.EQUALS)) {
				if(expression.op().equals(ComparisonOperator.GREATER)) {
					methodEditor.addInstruction(Opcode.opc_invokeinterface,greaterRef);
				}
				else if(expression.op().equals(ComparisonOperator.SMALLER)) {
					methodEditor.addInstruction(Opcode.opc_invokeinterface,smallerRef);
				}
				else if(expression.op().equals(ComparisonOperator.CONTAINS)) {
					methodEditor.addInstruction(Opcode.opc_invokeinterface,containsRef);
				}
				else if(expression.op().equals(ComparisonOperator.STARTSWITH)) {
					methodEditor.addInstruction(Opcode.opc_ldc, new Integer(1));
					methodEditor.addInstruction(Opcode.opc_invokeinterface,startsWithRef);
				}
				else if(expression.op().equals(ComparisonOperator.ENDSWITH)) {
					methodEditor.addInstruction(Opcode.opc_ldc, new Integer(1));
					methodEditor.addInstruction(Opcode.opc_invokeinterface,endsWithRef);
				}
				else {
					throw new RuntimeException("Cannot interpret constraint: "+expression.op());
				}
			}
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			methodEditor.addInstruction(Opcode.opc_invokeinterface,notRef);
		}
		
		private Iterator4 fieldNames(FieldValue fieldValue) {
			Collection4 coll=new Collection4();
			ComparisonOperand curOp=fieldValue;
			while(curOp instanceof FieldValue) {
				FieldValue curField=(FieldValue)curOp;
				coll.add(curField.fieldName());
				curOp=curField.parent();
			}
			return coll.iterator();
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
			expr.accept(new SODABloatMethodVisitor(predicateClass,classLoader));
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
		queryType = createType(Query.class);
		descendRef=createMethodReference(Query.class,"descend",new Class[]{String.class},Query.class);
		constrainRef=createMethodReference(Query.class,"constrain",new Class[]{Object.class},Constraint.class);
		greaterRef=createMethodReference(Constraint.class,"greater",new Class[]{},Constraint.class);
		smallerRef=createMethodReference(Constraint.class,"smaller",new Class[]{},Constraint.class);
		containsRef=createMethodReference(Constraint.class,"contains",new Class[]{},Constraint.class);
		startsWithRef=createMethodReference(Constraint.class,"startsWith",new Class[]{Boolean.TYPE},Constraint.class);
		endsWithRef=createMethodReference(Constraint.class,"endsWith",new Class[]{Boolean.TYPE},Constraint.class);
		notRef=createMethodReference(Constraint.class,"not",new Class[]{},Constraint.class);
		andRef=createMethodReference(Constraint.class,"and",new Class[]{Constraint.class},Constraint.class);
		orRef=createMethodReference(Constraint.class,"or",new Class[]{Constraint.class},Constraint.class);
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
	
	private MemberRef createMethodReference(Class parent,String name,Class[] args,Class ret) {
		Type[] argTypes=new Type[args.length];
		for (int argIdx = 0; argIdx < args.length; argIdx++) {
			argTypes[argIdx]=createType(args[argIdx]);
		}
		NameAndType nameAndType=new NameAndType(name,Type.getType(argTypes,createType(ret)));
		return new MemberRef(createType(parent),nameAndType);
	}

	private MemberRef createFieldReference(Class parentClass,Class fieldClass,String name) throws NoSuchFieldException {
		NameAndType nameAndType=new NameAndType(name,createType(fieldClass));
		return new MemberRef(createType(parentClass),nameAndType);
	}

	private Type createType(Class clazz) {
		return Type.getType(clazz);
	}
	
	private Class fieldClass(Class parent, String name) throws NoSuchFieldException {
		return parent.getDeclaredField(name).getType();
	}
	
	private static class FieldRootClassNameVisitor implements ComparisonOperandVisitor {
		private Class _predicateClass;
		private Class _candidateClass;
		private String _className;
		
		public FieldRootClassNameVisitor(Class predicateClass, Class candidateClass) {
			this._predicateClass = predicateClass;
			this._candidateClass = candidateClass;
		}

		public void visit(PredicateFieldRoot root) {
			_className=_predicateClass.getName();
		}

		public void visit(CandidateFieldRoot root) {
			_className=_candidateClass.getName();
		}

		public void visit(StaticFieldRoot root) {
			_className=root.className();
		}
		
		public String className() {
			return _className;
		}

		public void visit(ArithmeticExpression operand) {
		}

		public void visit(ConstValue operand) {
		}

		public void visit(FieldValue operand) {
		}

		public void visit(ArrayAccessValue operand) {
			// FIXME Auto-generated method stub
			
		}
	}
}
