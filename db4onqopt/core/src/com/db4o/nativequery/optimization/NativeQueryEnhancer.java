/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.cfg.FlowGraph;
import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassSource;
import EDU.purdue.cs.bloat.tree.PrintVisitor;

import com.db4o.instrumentation.core.*;
import com.db4o.nativequery.NQDebug;
import com.db4o.nativequery.analysis.BloatExprBuilderVisitor;
import com.db4o.nativequery.expr.Expression;

public class NativeQueryEnhancer {
	
	public static final String OPTIMIZE_QUERY_METHOD_NAME = "optimizeQuery";
	
	private static SODABloatMethodBuilder BLOAT_BUILDER=new SODABloatMethodBuilder();
	
	public boolean enhance(BloatLoaderContext bloatUtil,ClassEditor classEditor,String methodName,Type[] argTypes,ClassLoader classLoader,ClassSource classSource) throws Exception {
		if(NQDebug.LOG) {
			System.err.println("Enhancing "+classEditor.name());
		}
		Expression expr = analyze(bloatUtil, classEditor, methodName, argTypes);
		if(expr==null) {
			return false;
		}
		MethodEditor methodEditor=BLOAT_BUILDER.injectOptimization(expr,classEditor,classLoader,classSource);
		if(NQDebug.LOG) {
			System.out.println("SODA BYTE CODE:");
			methodEditor.print(System.out);
		}
		methodEditor.commit();
		classEditor.commit();
		return true;
	}
	
	public Expression analyze(BloatLoaderContext bloatUtil, ClassEditor classEditor, String methodName) {
		return analyze(bloatUtil, classEditor, methodName,null);
	}
	
	public Expression analyze(BloatLoaderContext bloatUtil, ClassEditor classEditor, String methodName, Type[] argTypes) {
		FlowGraph flowGraph=null;
		try {
			flowGraph=bloatUtil.flowGraph(classEditor,methodName, argTypes);
		} catch (ClassNotFoundException e) {
			return null;
		}
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
