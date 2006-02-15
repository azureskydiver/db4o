/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.query.*;


/**
 * @exclude
 * @partial
 */
public abstract class YapStream extends YapStreamBase implements ExtObjectContainer {
	YapStream(YapStream a_parent) {
		super(a_parent);
	}
	
	public ObjectSet query(Predicate predicate,Comparator comparator) {
		return query(predicate,new JdkComparatorWrapper(comparator));
	}
}