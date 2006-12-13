/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.YapHandlers;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class YapHandlersTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}

	public void testInterfaceHandlerIsSameAsObjectHandler() {
		final YapHandlers handlers = stream().handlers();
		Assert.areSame(
				handlers.handlerForClass(stream(), reflectClass(Object.class)),
				handlers.handlerForClass(stream(), reflectClass(FooInterface.class)));
	}

	private ReflectClass reflectClass(Class clazz) {
		return reflector().forClass(clazz);
	}
}
