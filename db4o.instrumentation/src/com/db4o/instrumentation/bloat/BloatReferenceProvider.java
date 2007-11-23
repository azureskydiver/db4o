/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.api.*;


public class BloatReferenceProvider implements ReferenceProvider {

	public FieldRef forField(Class declaringType, Class fieldType, String fieldName) {
		NameAndType nameAndType=new NameAndType(fieldName, typeRef(fieldType));
		return new BloatFieldRef(new MemberRef(typeRef(declaringType),nameAndType));
	}

	public MethodRef forMethod(Class declaringType, String methodName, Class[] parameterTypes, Class returnType) {
		Type[] argTypes=new Type[parameterTypes.length];
		for (int argIdx = 0; argIdx < parameterTypes.length; argIdx++) {
			argTypes[argIdx]=typeRef(parameterTypes[argIdx]);
		}
		NameAndType nameAndType=new NameAndType(methodName, Type.getType(argTypes, typeRef(returnType)));
		return new BloatMethodRef(new MemberRef(typeRef(declaringType), nameAndType));
	}

	Type typeRef(Class clazz) {
		return Type.getType(clazz);
	}

}
