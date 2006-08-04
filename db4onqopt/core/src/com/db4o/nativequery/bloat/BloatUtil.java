/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.bloat;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

public class BloatUtil {
	private ClassFileLoader loader;
	private CachingBloatContext context;
	
	public BloatUtil(ClassFileLoader loader) {
		this.loader=loader;
		context=new CachingBloatContext(loader,new LinkedList(),false);
	}
	
	public FlowGraph flowGraph(String className, String methodName) throws ClassNotFoundException {
		ClassEditor classEdit = classEditor(className);
		return flowGraph(classEdit, methodName);
	}

	// FIXME handle overloaded
	public FlowGraph flowGraph(ClassEditor classEdit, String methodName) throws ClassNotFoundException {
		MethodInfo[] methods = classEdit.methods();
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			MethodInfo methodInfo=methods[methodIdx];
			MethodEditor methodEdit = new MethodEditor(classEdit, methodInfo);
			if (methodEdit.name().equals(methodName)) {
				// methodEdit.print(System.out);
				return new FlowGraph(methodEdit);
			}
		}
		Type superType = classEdit.superclass();
		if(superType!=null) {
			return flowGraph(normalizedClassName(superType),methodName);
		}
		return null;
	}

	public ClassEditor classEditor(String className) throws ClassNotFoundException {
		return new ClassEditor(context, loader.loadClass(className));
	}

	public String normalizedClassName(Type type) {
		return type.className().replace('/', '.');
	}
}
