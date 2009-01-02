/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;

/**
 * Enables the Transparent Update and Transparent Activation behaviors.
 */
public class TransparentPersistenceSupport extends TransparentActivationSupport {

	private final RollbackStrategy _rollbackStrategy;

	public TransparentPersistenceSupport(RollbackStrategy rollbackStrategy) {
		_rollbackStrategy = rollbackStrategy;
	}
	
	public TransparentPersistenceSupport() {
		this(null);
	}

	@Override
	public void apply(InternalObjectContainer container) {
		super.apply(container);
		
		final TransparentActivationDepthProvider provider = (TransparentActivationDepthProvider) container.configImpl().activationDepthProvider();
		provider.enableTransparentPersistenceSupportFor(container, _rollbackStrategy);
	}
}
