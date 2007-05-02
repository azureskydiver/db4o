package com.db4o.ta.instrumentation;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.*;
import com.db4o.ta.*;
import com.db4o.ta.internal.*;

/*
 * TODO: COR-591 - Lots of copy & paste from db4onqopt/Db4oEnhancingClassLoader, BloatUtil, SODABloatMethodBuilder, etc.
 */

public class TransparentActivationClassLoader extends BloatingClassLoader {

	private Map _cache = new HashMap();
	private ClassFilter _filter;

	public TransparentActivationClassLoader(URL[] urls, ClassLoader parent, ClassFilter filter) {
		super(urls, parent);
		_filter = filter;
	}

	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(_cache.containsKey(name)) {
			return (Class)_cache.get(name);
		}
		Class delegateClass = super.loadClass(name,resolve);
		
		if(mustDelegate(name)) {
			return delegateClass;
		}
		Class clazz=(_filter.accept(delegateClass) ? findClass(name) : findRawClass(name));
		_cache.put(clazz.getName(), clazz);
		if(resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private boolean mustDelegate(String name) {
		return name.startsWith("java.")
				|| name.startsWith("javax.")
				||name.startsWith("sun.")
				||((name.startsWith("com.db4o.") && name.indexOf("test.")<0));
	}

	private Class findRawClass(String className) throws ClassNotFoundException {
        try {
			String resourcePath = className.replace('.','/') + ".class";
			InputStream resourceStream = getResourceAsStream(resourcePath);
			ByteArrayOutputStream rawByteStream = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int bytesread = 0;
			while((bytesread = resourceStream.read(buf)) >= 0) {
				rawByteStream.write(buf, 0, bytesread);
			}
			resourceStream.close();
			byte[] rawBytes = rawByteStream.toByteArray();
			return super.defineClass(className, rawBytes, 0, rawBytes.length);
		} catch (Exception exc) {
			throw new ClassNotFoundException(className, exc);
		}	
	}

	protected void bloat(ClassEditor ce) {
		ce.addInterface(Activatable.class);
		createActivatorField(ce);
		createBindMethod(ce);
	}

	private void createActivatorField(ClassEditor ce) {
		FieldEditor fieldEditor = new FieldEditor(ce, Modifiers.PRIVATE | Modifiers.TRANSIENT, Type.getType(Activator.class), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		fieldEditor.commit();
	}

	private void createBindMethod(ClassEditor ce) {
		final Type activatorType = Type.getType(Activator.class);
		final Type objectContainerType = Type.getType(ObjectContainer.class);
		String methodName = TransparentActivationInstrumentationConstants.BIND_METHOD_NAME;
		Type[] paramTypes = { objectContainerType };
		MethodEditor methodEditor = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID, methodName, paramTypes, new Type[] {});
		methodEditor.addLabel(new Label(0,true));
		methodEditor.addInstruction(Opcode.opc_aload, new LocalVariable(0));
		methodEditor.addInstruction(Opcode.opc_new,activatorType);
		methodEditor.addInstruction(Opcode.opc_dup);
		methodEditor.addInstruction(Opcode.opc_aload, new LocalVariable(1));
		methodEditor.addInstruction(Opcode.opc_aload, new LocalVariable(0));
		methodEditor.addInstruction(Opcode.opc_invokespecial, createMethodReference(activatorType, "<init>", new Type[] { objectContainerType, Type.OBJECT }, Type.VOID));
		methodEditor.addInstruction(Opcode.opc_putfield, createFieldReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME, activatorType));
		
		methodEditor.addInstruction(Opcode.opc_return);
		methodEditor.commit();
	}

	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}

	private MemberRef createFieldReference(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}

}
