/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

/**
 * @exclude
 * @sharpen.partial
 */
public abstract class ObjectContainerBase extends PartialObjectContainer {
	
	public ObjectContainerBase(Configuration config,ObjectContainerBase a_parent) {
		super(config,a_parent);
	}
	
	public ObjectSet query(Predicate predicate,Comparator comparator) {
		return query(null, predicate,new JdkComparatorWrapper(comparator));
	}
}