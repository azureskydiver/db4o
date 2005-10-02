package com.db4o.nativequery.optimization.db4o;

import junit.framework.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;

public class SODABloatMethodBuilderTest extends TestCase {
	private final static String[] FIELDNAMES={"foo","baz"};
	private final static Object CONSTVALUE="X";
	
	private ClassEditor classEditor;
	private SODABloatMethodBuilder builder;
	
	protected void setUp() throws Exception {
		classEditor=BloatUtil.classEditor(new ClassFileLoader(),getClass().getName());
		builder=new SODABloatMethodBuilder();
	}
	
	public void testComparison() {
		FieldValue left=new FieldValue(1,FIELDNAMES);
		ConstValue right=new ConstValue(CONSTVALUE);
		
		Expression expr=new ComparisonExpression(left,right,ComparisonOperator.SMALLER);
		expr=new AndExpression(expr,new NotExpression(expr));
		MethodEditor methodEditor=builder.injectOptimization(expr, classEditor, getClass().getClassLoader());
		methodEditor.print(System.out);
		FlowGraph flowGraph=new FlowGraph(methodEditor);
		flowGraph.visit(new PrintVisitor());
	}
}
