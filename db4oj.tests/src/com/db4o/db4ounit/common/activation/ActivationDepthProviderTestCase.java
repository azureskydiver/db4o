/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import com.db4o.config.*;
import com.db4o.db4ounit.common.foundation.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * Ensures the container uses the provided ActivationDepthProvider instance
 * whenever necessary.
 * 
 * FIXME: dont OptOutCS
 */
public class ActivationDepthProviderTestCase extends AbstractDb4oTestCase implements OptOutCS {
	
	public final class Item {
	}
	
	private final MockActivationDepthProvider _dummyProvider = new MockActivationDepthProvider();
	
	protected void configure(Configuration config) throws Exception {
		((Config4Impl)config).activationDepthProvider(_dummyProvider);
	}
	
	protected void store() throws Exception {
		store(new Item());
	}
	
	public void testDefaultActivationDepth() {
		queryItem();
		assertProviderCalled("activationDepthFor", classMetada(Item.class));
	}

	public void testSpecificActivationDepth() {
		
		Item item = queryItem();		
		resetProvider();
		
		db().activate(item, 3);
		
		assertProviderCalled("activationDepth", new Integer(3));
	}
	
	private void assertProviderCalled(String methodName, Object arg) {
		Iterator4Assert.areEqual(
			new Object[] { new MethodCall(methodName, arg) },
			_dummyProvider.iterator());
	}

	private ClassMetadata classMetada(Class klass) {
		return stream().classMetadataForReflectClass(reflectClass(klass));
	}
	
	private void resetProvider() {
		_dummyProvider.reset();
	}

	private Item queryItem() {
		return (Item)retrieveOnlyInstance(Item.class);
	}
}
