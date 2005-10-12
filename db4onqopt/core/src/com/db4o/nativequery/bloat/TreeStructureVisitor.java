package com.db4o.nativequery.bloat;

import EDU.purdue.cs.bloat.tree.*;

public class TreeStructureVisitor extends TreeVisitor {
	private int depth=0;
	
	public void visitNode(Node node) {
		for(int idx=0;idx<depth;idx++) {
			System.out.print("--");
		}
		System.out.println(node.getClass().getSimpleName()+" : "+node);
		depth++;
		super.visitNode(node);
		depth--;
	}
}
