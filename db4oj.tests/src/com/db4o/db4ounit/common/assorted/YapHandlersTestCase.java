/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class YapHandlersTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}

	public void testInterfaceHandlerIsSameAsObjectHandler() {
		Assert.areSame(
				handlerForClass(Object.class),
				handlerForClass(FooInterface.class));
	}

	private TypeHandler4 handlerForClass(Class clazz) {
		return handlers().handlerForClass(stream(), reflectClass(clazz));
	}

	private YapHandlers handlers() {
		return stream().handlers();
	}

	private ReflectClass reflectClass(Class clazz) {
		return reflector().forClass(clazz);
	}
}
