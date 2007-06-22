/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.analysis;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;

import db4ounit.*;

public abstract class NQOptimizationByteCodeTestCaseBase implements TestCase {

	public final static int NO_MODIFIERS = 0;
	public final static String CLASSNAME = "invalid.CandidateClass";
	private static final String METHODNAME = "sampleMethod";
	
	protected final LocalVariable THIS_VAR = new LocalVariable(0);
	protected final LocalVariable DATA_VAR = new LocalVariable(1);
	private final LabelGenerator _labelGenerator = new LabelGenerator();
	protected final Label START_LABEL = _labelGenerator.createLabel(true);
	
	public void testOptimization() throws Exception {
		BloatUtil bloatUtil = new BloatUtil(new ClassFileLoader());
		ClassEditor clazz = bloatUtil.classEditor(NO_MODIFIERS, CLASSNAME, Type.OBJECT, new Type[]{});
		MethodEditor method = new MethodEditor(clazz, NO_MODIFIERS, Type.BOOLEAN, METHODNAME, new Type[]{ Type.getType(Data.class) }, new Type[]{});
		method.addLabel(START_LABEL);
		generateMethodBody(method);
		method.commit();
		clazz.commit();
		
		FlowGraph flowGraph = new FlowGraph(method);
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(bloatUtil);
		flowGraph.visit(visitor);
		Expression expression = visitor.expression();
		assertOptimization(expression);
	}
	
	protected MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}

	protected MemberRef createFieldReference(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}
	
	protected Label createLabel() {
		return createLabel(true);
	}
	
	protected Label createLabel(boolean startsBlock) {
		return _labelGenerator.createLabel(startsBlock);
	}
	
	protected abstract void generateMethodBody(MethodEditor method);
	
	protected abstract void assertOptimization(Expression expression) throws Exception;
}
