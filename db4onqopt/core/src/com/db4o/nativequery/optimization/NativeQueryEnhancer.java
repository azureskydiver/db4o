/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.ClassSource;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.nativequery.*;
import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.reflect.Reflector;

public class NativeQueryEnhancer {
	
	private static SODABloatMethodBuilder BLOAT_BUILDER=new SODABloatMethodBuilder();
	
	public void enhance(BloatUtil bloatUtil,ClassEditor classEditor,String methodName,Type[] argTypes,ClassLoader classLoader,ClassSource classSource) throws Exception {
		if(NQDebug.LOG) {
			System.err.println("Enhancing "+classEditor.name());
		}
		Expression expr = analyze(bloatUtil, classEditor, methodName, argTypes,classSource);
		if(expr==null) {
			return;
		}
		MethodEditor methodEditor=BLOAT_BUILDER.injectOptimization(expr,classEditor,classLoader,classSource);
		if(NQDebug.LOG) {
			System.out.println("SODA BYTE CODE:");
			methodEditor.print(System.out);
		}
		methodEditor.commit();
		classEditor.commit();
	}
	
	public Expression analyze(BloatUtil bloatUtil, ClassEditor classEditor, String methodName, ClassSource classSource) {
		return analyze(bloatUtil, classEditor, methodName,null,classSource);
	}
	
	public Expression analyze(BloatUtil bloatUtil, ClassEditor classEditor, String methodName, Type[] argTypes, ClassSource classSource) {
		FlowGraph flowGraph=null;
		try {
			flowGraph=bloatUtil.flowGraph(classEditor,methodName, argTypes);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if(flowGraph!=null) {
			BloatExprBuilderVisitor builder = new BloatExprBuilderVisitor(bloatUtil, classSource);
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
