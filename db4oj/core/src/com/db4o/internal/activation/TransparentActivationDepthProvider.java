/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.activation;

import com.db4o.internal.*;
import com.db4o.ta.*;

/**
 * @exclude
 */
public interface TransparentActivationDepthProvider extends ActivationDepthProvider{

	void enableTransparentPersistenceSupportFor(
			InternalObjectContainer container, RollbackStrategy rollbackStrategy);

	void addModified(Transaction transaction, Object object);

}