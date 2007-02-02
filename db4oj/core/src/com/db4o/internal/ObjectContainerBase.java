/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.config.*;
import com.db4o.ext.*;



/**
 * @exclude
 * @sharpen.partial
 * @sharpen.ignore
 */
public abstract class ObjectContainerBase extends PartialObjectContainer implements ExtObjectContainer {
	
	public ObjectContainerBase(Configuration config,ObjectContainerBase a_parent) {
		super(config,a_parent);
	}

}