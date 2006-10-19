/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;


/**
 * @exclude
 * @sharpen.partial
 */
public abstract class YapStream extends YapStreamBase implements ExtObjectContainer {
	
	public YapStream(Configuration config,YapStream a_parent) {
		super(config,a_parent);
	}
	
	public ObjectSet query(Predicate predicate,Comparator comparator) {
		return query(predicate,new JdkComparatorWrapper(comparator));
	}
}