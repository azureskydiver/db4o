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

	public FlowGraph flowGraph(ClassEditor classEdit, String methodName) {
		MethodInfo[] methods = classEdit.methods();
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			MethodInfo methodInfo=methods[methodIdx];
			MethodEditor methodEdit = new MethodEditor(classEdit, methodInfo);
			if (methodEdit.name().equals(methodName)) {
				return new FlowGraph(methodEdit);
			}
		}
		return null;
	}

	public ClassEditor classEditor(String className) throws ClassNotFoundException {
		return new ClassEditor(context, loader.loadClass(className));
	}
}
