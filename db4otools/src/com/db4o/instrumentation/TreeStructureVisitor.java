/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation;

import EDU.purdue.cs.bloat.tree.*;

public class TreeStructureVisitor extends TreeVisitor {
	private int depth=0;
	
	public void visitNode(Node node) {
		for(int idx=0;idx<depth;idx++) {
			System.out.print("--");
		}
		System.out.println(node.getClass().getName()+" : "+node);
		depth++;
		super.visitNode(node);
		depth--;
	}
}
