/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.Label;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassSource;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.instrumentation.*;
import com.db4o.internal.query.Db4oEnhancedFilter;
import com.db4o.nativequery.expr.AndExpression;
import com.db4o.nativequery.expr.BoolConstExpression;
import com.db4o.nativequery.expr.ComparisonExpression;
import com.db4o.nativequery.expr.Expression;
import com.db4o.nativequery.expr.ExpressionVisitor;
import com.db4o.nativequery.expr.NotExpression;
import com.db4o.nativequery.expr.OrExpression;
import com.db4o.nativequery.expr.cmp.ComparisonOperand;
import com.db4o.nativequery.expr.cmp.ComparisonOperator;
import com.db4o.nativequery.expr.cmp.FieldValue;
import com.db4o.query.Constraint;
import com.db4o.query.Query;

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
	private Type queryType;

	private class SODABloatMethodVisitor implements ExpressionVisitor {

		private Class predicateClass;
		private Class candidateClass;
		private ClassSource classSource;
		
		public SODABloatMethodVisitor(Class predicateClass, ClassLoader classLoader, ClassSource classSource) {
			this.predicateClass=predicateClass;
			this.classSource = classSource;
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
			methodEditor.addInstruction(Opcode.opc_aload, new LocalVariable(
					"query", queryType, 1));
			Iterator4 fieldNames = fieldNames(expression.left());
			while (fieldNames.moveNext()) {
				methodEditor.addInstruction(Opcode.opc_ldc, fieldNames.current());
				methodEditor.addInstruction(Opcode.opc_invokeinterface, descendRef);
			}
			expression.right().accept(
					new ComparisonBytecodeGeneratingVisitor(methodEditor,
							predicateClass, candidateClass, classSource));
			methodEditor.addInstruction(Opcode.opc_invokeinterface,
					constrainRef);
			ComparisonOperator op = expression.op();
			if (op.equals(ComparisonOperator.EQUALS)) {
				return;
			}
			if (op.equals(ComparisonOperator.IDENTITY)) {
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						identityRef);
				return;
			}
			if (op.equals(ComparisonOperator.GREATER)) {
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						greaterRef);
				return;
			}
			if (op.equals(ComparisonOperator.SMALLER)) {
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						smallerRef);
				return;
			}
			if (op.equals(ComparisonOperator.CONTAINS)) {
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						containsRef);
				return;
			}
			if (op.equals(ComparisonOperator.STARTSWITH)) {
				methodEditor.addInstruction(Opcode.opc_ldc, new Integer(1));
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						startsWithRef);
				return;
			}
			if (op.equals(ComparisonOperator.ENDSWITH)) {
				methodEditor.addInstruction(Opcode.opc_ldc, new Integer(1));
				methodEditor.addInstruction(Opcode.opc_invokeinterface,
						endsWithRef);
				return;
			}
			throw new RuntimeException("Cannot interpret constraint: "
					+ op);
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
				coll.prepend(curField.fieldName());
				curOp=curField.parent();
			}
			return coll.iterator();
		}
	}
	
	public SODABloatMethodBuilder() {
		buildMethodReferences();
	}
	
	public MethodEditor injectOptimization(Expression expr, ClassEditor classEditor,ClassLoader classLoader, ClassSource classSource) {
		classEditor.addInterface(Db4oEnhancedFilter.class);
		methodEditor=new MethodEditor(classEditor,Modifiers.PUBLIC,Void.TYPE,"optimizeQuery",new Class[]{Query.class},new Class[]{});
		LabelGenerator labelGen = new LabelGenerator();
		methodEditor.addLabel(labelGen.createLabel(true));
		try {
			Class predicateClass = classLoader.loadClass(BloatUtil.normalizeClassName(classEditor.name()));
			expr.accept(new SODABloatMethodVisitor(predicateClass,classLoader,classSource));
			methodEditor.addInstruction(Opcode.opc_pop);
			methodEditor.addLabel(labelGen.createLabel(false));
			methodEditor.addInstruction(Opcode.opc_return);
			methodEditor.addLabel(labelGen.createLabel(true));
			if(LOG_BYTECODE) {
				methodEditor.print(System.out);
			}
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
}
