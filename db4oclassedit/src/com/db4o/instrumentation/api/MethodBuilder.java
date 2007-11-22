/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.api;

import java.io.*;
import java.lang.reflect.*;

/**
 * Cross platform interface for bytecode emission.
 */
public interface MethodBuilder {
	
	ReferenceProvider references();
	
	void ldc(Object value);
	
	void loadArgument(int index);
	
	void pop();

	void loadArrayElement(Class elementType);

	void add(Class operandType);

	void subtract(Class operandType);

	void multiply(Class operandType);

	void divide(Class operandType);

	void invoke(MethodRef method);
	
	void invoke(Method method);	

	void loadField(FieldRef fieldRef);

	void loadStaticField(FieldRef fieldRef);
	
	void box(Class boxedType);
	
	void endMethod();
	
	void print(PrintStream out);
}
