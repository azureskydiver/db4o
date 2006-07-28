/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.*;
import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;

public class NativeQueryEnhancer {
	
	private static SODABloatMethodBuilder BLOAT_BUILDER=new SODABloatMethodBuilder();
	
	public void enhance(BloatUtil bloatUtil,ClassEditor classEditor,String methodName,ClassLoader classLoader) throws Exception {
		if(NQDebug.LOG) {
			System.err.println("Enhancing "+classEditor.name());
		}
		Expression expr = analyze(bloatUtil, classEditor, methodName);
		if(expr==null) {
			return;
		}
		MethodEditor methodEditor=BLOAT_BUILDER.injectOptimization(expr,classEditor,classLoader);
		if(NQDebug.LOG) {
			System.out.println("SODA BYTE CODE:");
			methodEditor.print(System.out);
		}
		methodEditor.commit();
		classEditor.commit();
	}

	public Expression analyze(BloatUtil bloatUtil, ClassEditor classEditor, String methodName) {
		FlowGraph flowGraph=bloatUtil.flowGraph(classEditor,methodName);
		if(flowGraph!=null) {
			BloatExprBuilderVisitor builder = new BloatExprBuilderVisitor(bloatUtil);
			if(NQDebug.LOG) {
				System.out.println("FLOW GRAPH:");
				flowGraph.visit(new PrintVisitor());
			}
			flowGraph.visit(builder);
			Expression expr=builder.expression();
			if(NQDebug.LOG) {
				System.out.println("EXPRESSION TREE:");
				System.out.println(expr);
			}
			return expr;
		}
		return null;
	}
}
