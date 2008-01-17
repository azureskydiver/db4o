/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * Enables the Transparent Update and Transparent Activation behaviors.
 */
public class TransparentPersistenceSupport implements ConfigurationItem {

	private final RollbackStrategy _rollbackStrategy;

	public TransparentPersistenceSupport(RollbackStrategy rollbackStrategy) {
		_rollbackStrategy = rollbackStrategy;
	}
	
	public TransparentPersistenceSupport() {
		this(null);
	}

	public void apply(InternalObjectContainer container) {
	}

	public void prepare(Configuration configuration) {
		configuration.add(new TransparentActivationSupport());
	}

	public void rollback(ObjectContainer container, ObjectInfo objectInfo) {
		if (null == _rollbackStrategy) {
			return;
		}
		_rollbackStrategy.rollback(container, objectInfo);
	}
}
