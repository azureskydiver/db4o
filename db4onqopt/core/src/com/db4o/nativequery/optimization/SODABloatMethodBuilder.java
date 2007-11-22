/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.foundation.*;
import com.db4o.instrumentation.util.*;
import com.db4o.internal.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.query.*;

public class SODABloatMethodBuilder {	
	private final static boolean LOG_BYTECODE=false;
	
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
	private MemberRef identityRef;

	private class SODABloatMethodVisitor implements ExpressionVisitor {

		private Class predicateClass;
		private ClassSource classSource;
		
		public SODABloatMethodVisitor(Class predicateClass, ClassSource classSource) {
			this.predicateClass=predicateClass;
			this.classSource = classSource;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			invoke(andRef);
		}

		public void visit(BoolConstExpression expression) {
			loadQuery();
			//throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		private void loadQuery() {
			methodEditor.addInstruction(Opcode.opc_aload, new LocalVariable(1));
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			invoke(orRef);
		}

		public void visit(final ComparisonExpression expression) {
			loadQuery();
			
			descend(fieldNames(expression.left()));
			
			expression.right().accept(comparisonEmitter());
			
			constrain(expression.op());
		}

		private void descend(Iterator4 fieldNames) {
			while (fieldNames.moveNext()) {
				descend(fieldNames.current());
			}
		}

		private ComparisonBytecodeGeneratingVisitor comparisonEmitter() {
			return new ComparisonBytecodeGeneratingVisitor(methodEditor, predicateClass, classSource);
		}

		private void constrain(ComparisonOperator op) {
			invoke(constrainRef);
			
			if (op.equals(ComparisonOperator.EQUALS)) {
				return;
			}
			if (op.equals(ComparisonOperator.IDENTITY)) {
				invoke(identityRef);
				return;
			}
			if (op.equals(ComparisonOperator.GREATER)) {
				invoke(greaterRef);
				return;
			}
			if (op.equals(ComparisonOperator.SMALLER)) {
				invoke(smallerRef);
				return;
			}
			if (op.equals(ComparisonOperator.CONTAINS)) {
				invoke(containsRef);
				return;
			}
			if (op.equals(ComparisonOperator.STARTSWITH)) {
				ldc(new Integer(1));
				invoke(startsWithRef);
				return;
			}
			if (op.equals(ComparisonOperator.ENDSWITH)) {
				ldc(new Integer(1));
				invoke(endsWithRef);
				return;
			}
			throw new RuntimeException("Cannot interpret constraint: "
					+ op);
		}

		private void descend(final Object fieldName) {
			ldc(fieldName);
			invoke(descendRef);
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			invoke(notRef);
		}
		
		private Iterator4 fieldNames(FieldValue fieldValue) {
			Collection4 coll=new Collection4();
			ComparisonOperand curOp=fieldValue;
			while(curOp instanceof FieldValue) {
				FieldValue curField=(FieldValue)curOp;
				coll.prepend(curField.fieldName());
				curOp=curField.parent();
			}
			return coll.iterator();
		}
	}
	
	public SODABloatMethodBuilder() {
		buildMethodReferences();
	}
	
	public void injectOptimization(Expression expr, ClassEditor classEditor,ClassLoader classLoader, ClassSource classSource) {
		classEditor.addInterface(Db4oEnhancedFilter.class);
		methodEditor=new MethodEditor(classEditor,Modifiers.PUBLIC,Void.TYPE,NativeQueryEnhancer.OPTIMIZE_QUERY_METHOD_NAME,new Class[]{Query.class},new Class[]{});
		LabelGenerator labelGen = new LabelGenerator();
		methodEditor.addLabel(labelGen.createLabel(true));
		try {
			Class predicateClass = classLoader.loadClass(BloatUtil.normalizeClassName(classEditor.name()));
			expr.accept(new SODABloatMethodVisitor(predicateClass,classSource));
			methodEditor.addInstruction(Opcode.opc_pop);
			methodEditor.addLabel(labelGen.createLabel(false));
			methodEditor.addInstruction(Opcode.opc_return);
			methodEditor.addLabel(labelGen.createLabel(true));
			if(LOG_BYTECODE) {
				methodEditor.print(System.out);
			}
			methodEditor.commit();
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
	
	private void buildMethodReferences() {
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
		identityRef=createMethodReference(Constraint.class,"identity",new Class[]{},Constraint.class);
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
	
	private void invoke(final MemberRef method) {
		methodEditor.addInstruction(Opcode.opc_invokeinterface, method);
	}
	
	private void ldc(Object value) {
		methodEditor.addInstruction(Opcode.opc_ldc, value);
	}
}
