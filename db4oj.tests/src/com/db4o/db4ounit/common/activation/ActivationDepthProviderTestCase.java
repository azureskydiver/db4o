/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import com.db4o.config.Configuration;
import com.db4o.db4ounit.common.foundation.Iterator4Assert;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;

import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * Ensures the container uses the provided ActivationDepthProvider instance
 * whenever necessary.
 */
public class ActivationDepthProviderTestCase extends AbstractDb4oTestCase  {
	
	public static final class Item {
	}
	
	private final MockActivationDepthProvider _dummyProvider = new MockActivationDepthProvider();
	
	protected void configure(Configuration config) throws Exception {
		((Config4Impl)config).activationDepthProvider(_dummyProvider);
	}
	
	protected void store() throws Exception {
		store(new Item());
	}
	
	public void testCSActivationDepthFor() {
		if (!isNetworkCS()) {
			return;
		}
		
		resetProvider();
		queryItem();
		assertProviderCalled(new MethodCall[] {
			new MethodCall("activationDepthFor", itemMetadata(), ActivationMode.PREFETCH),
			new MethodCall("activationDepthFor", itemMetadata(), ActivationMode.ACTIVATE),
		});
	}
	
	public void testSoloActivationDepthFor() {
		if (isNetworkCS()) {
			return;
		}
		resetProvider();
		queryItem();
		assertProviderCalled("activationDepthFor", itemMetadata(), ActivationMode.ACTIVATE);
	}

	private boolean isNetworkCS() {
		return isClientServer() && !isMTOC();
	}

	private ClassMetadata itemMetadata() {
		return classMetadataFor(Item.class);
	}

	public void testSpecificActivationDepth() {
		
		Item item = queryItem();		
		resetProvider();
		
		db().activate(item, 3);
		
		assertProviderCalled("activationDepth", new Integer(3), ActivationMode.ACTIVATE);
	}
	
	private void assertProviderCalled(MethodCall[] expectedCalls) {
		Iterator4Assert.areEqual(
			expectedCalls,
			_dummyProvider.iterator());
	}
	
	private void assertProviderCalled(String methodName, Object arg1, Object arg2) {
		Iterator4Assert.areEqual(
			new Object[] { new MethodCall(methodName, arg1, arg2) },
			_dummyProvider.iterator());
	}

	private void resetProvider() {
		_dummyProvider.reset();
	}

	private Item queryItem() {
		return (Item)retrieveOnlyInstance(Item.class);
	}
}
