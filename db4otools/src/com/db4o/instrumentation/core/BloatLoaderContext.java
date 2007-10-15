/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.core;

import java.util.*;

import com.db4o.instrumentation.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

public class BloatLoaderContext {
	private ClassInfoLoader loader;
	private EditorContext context;
	
	public BloatLoaderContext(ClassInfoLoader loader) {
		this(loader, new CachingBloatContext(loader,new LinkedList(),false));
	}

	public BloatLoaderContext(ClassInfoLoader loader, EditorContext context) {
		this.loader=loader;
		this.context=context;
	}

	public FlowGraph flowGraph(String className, String methodName) throws ClassNotFoundException {
		return flowGraph(className, methodName, null);
	}
	
	public FlowGraph flowGraph(String className, String methodName,Type[] argTypes) throws ClassNotFoundException {
		ClassEditor classEdit = classEditor(className);
		return flowGraph(classEdit, methodName, argTypes);
	}

	public FlowGraph flowGraph(ClassEditor classEdit, String methodName,Type[] argTypes) throws ClassNotFoundException {
		MethodEditor method = method(classEdit, methodName, argTypes);
		return method == null ? null : new FlowGraph(method);
	}

	public MethodEditor method(ClassEditor classEdit, String methodName,Type[] argTypes) throws ClassNotFoundException {
		ClassEditor clazz = classEdit;
		while(clazz != null) {
			MethodInfo[] methods = clazz.methods();
			for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
				MethodInfo methodInfo=methods[methodIdx];
				MethodEditor methodEdit = new MethodEditor(clazz, methodInfo);
				if (methodEdit.name().equals(methodName)&&signatureMatchesTypes(argTypes, methodEdit)) {
					return methodEdit;
				}
			}
			clazz = classEditor(clazz.superclass());
		}
		return null;
	}

	public ClassEditor classEditor(Type type) throws ClassNotFoundException {
		return type == null ? null : classEditor(BloatUtil.normalizeClassName(type));
	}

	private boolean signatureMatchesTypes(Type[] argTypes,
			MethodEditor methodEdit) {
		if(argTypes==null) {
			return true;
		}
		Type[] sigTypes=methodEdit.paramTypes();
		int sigOffset=(methodEdit.isStatic()||methodEdit.isConstructor() ? 0 : 1);
		if(argTypes.length!=(sigTypes.length-sigOffset)) {
			return false;
		}
		for (int idx = 0; idx < argTypes.length; idx++) {
			if(!argTypes[idx].className().equals(sigTypes[idx+sigOffset].className())) {
				return false;
			}
		}
		return true;
	}

	public ClassEditor classEditor(String className) throws ClassNotFoundException {
		return new ClassEditor(context, loader.loadClass(className));
	}

	public ClassEditor classEditor(int modifiers, String className, Type superClass, Type[] interfaces) {
		return new ClassEditor(context, modifiers, className, superClass, interfaces);
	}
	
	public Type superType(Type type) throws ClassNotFoundException {
		ClassInfo classInfo = loader.loadClass(type.className());
		return new ClassEditor(new CachingBloatContext(loader,new ArrayList(),false),classInfo).superclass();
	}
	
	public void commit() {
		try {
			context.commit();
		}
		catch(ConcurrentModificationException exc) {
			exc.printStackTrace();
			throw exc;
		}
	}
}
