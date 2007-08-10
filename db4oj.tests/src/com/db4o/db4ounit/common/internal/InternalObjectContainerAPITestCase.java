package com.db4o.db4ounit.common.internal;

import com.db4o.internal.ClassMetadata;
import com.db4o.internal.InternalObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class InternalObjectContainerAPITestCase extends AbstractDb4oTestCase {

	public static class Item {
	}

	protected void store() throws Exception {
		store(new Item());
	}
	
	public void testClassMetadataForName() {
		ClassMetadata clazz = ((InternalObjectContainer)db()).classMetadataForName(Item.class.getName());
		Assert.areEqual(Item.class.getName(), clazz.getName());
		Assert.areEqual(reflector().forClass(Item.class), clazz.classReflector());
	}
}
