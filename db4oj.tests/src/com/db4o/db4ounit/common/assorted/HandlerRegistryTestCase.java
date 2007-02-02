/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.internal.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class HandlerRegistryTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}

	public void _testInterfaceHandlerIsSameAsObjectHandler() {
		Assert.areSame(
				handlerForClass(Object.class),
				handlerForClass(FooInterface.class));
	}

	private TypeHandler4 handlerForClass(Class clazz) {
		return handlers().handlerForClass(stream(), reflectClass(clazz));
	}

	private HandlerRegistry handlers() {
		return stream().handlers();
	}
}
