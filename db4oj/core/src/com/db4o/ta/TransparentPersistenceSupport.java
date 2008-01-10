/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta;

import com.db4o.config.*;
import com.db4o.internal.*;

/**
 * Enables the Transparent Update and Transparent Activation behaviors.
 */
public class TransparentPersistenceSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
	}

	public void prepare(Configuration configuration) {
		configuration.add(new TransparentActivationSupport());
	}
}
