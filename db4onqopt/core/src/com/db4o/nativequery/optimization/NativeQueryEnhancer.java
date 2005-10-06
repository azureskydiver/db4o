package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;

public class NativeQueryEnhancer {
	private static SODABloatMethodBuilder BLOAT_BUILDER=new SODABloatMethodBuilder();
	
	public void enhance(ClassFileLoader loader,ClassEditor classEditor,String methodName,ClassLoader classLoader) throws Exception {
		System.err.println("Enhancing "+classEditor.name());
		Expression expr = analyze(loader, classEditor, methodName);

		MethodEditor methodEditor=BLOAT_BUILDER.injectOptimization(expr,classEditor,classLoader);
		System.out.println("SODA BYTE CODE:");
		methodEditor.print(System.out);
		methodEditor.commit();
		classEditor.commit();
	}

	public Expression analyze(ClassFileLoader loader, ClassEditor classEditor, String methodName) {
		FlowGraph flowGraph=BloatUtil.flowGraph(classEditor,methodName);
		BloatExprBuilderVisitor builder = new BloatExprBuilderVisitor(loader);
		//System.out.println("FLOW GRAPH:");
		//flowGraph.visit(new PrintVisitor());
		flowGraph.visit(builder);
		Expression expr=builder.expression();
		//System.out.println("EXPRESSION TREE:");
		//System.out.println(expr);
		return expr;
	}
}
