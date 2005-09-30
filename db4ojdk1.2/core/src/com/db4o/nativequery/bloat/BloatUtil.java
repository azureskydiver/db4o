package com.db4o.nativequery.bloat;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

public class BloatUtil {
	public static FlowGraph flowGraph(ClassFileLoader loader,String className, String methodName) throws ClassNotFoundException {
		ClassEditor classEdit = classEditor(loader,className);
		return flowGraph(classEdit, methodName);
	}

	public static FlowGraph flowGraph(ClassEditor classEdit, String methodName) {
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

	public static ClassEditor classEditor(ClassFileLoader loader,String className) throws ClassNotFoundException {
		return new ClassEditor(null, loader.loadClass(className));
	}
}
