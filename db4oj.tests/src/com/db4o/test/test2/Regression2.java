/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.test2;

import com.db4o.test.*;
import com.db4o.test.types.*;

/**
 * @decaf.ignore.jdk11
 */
public class Regression2 extends Regression{

	public RTestable[] testClasses(){
		return new RTestable[]{
			new RArrayList(),
			new RHashMap(),
			new RHashSet(),
			new RLinkedList(),
			new RTreeMap(),
			new RTreeMapComparator(),
			new RTreeSet()
		};
	}
}