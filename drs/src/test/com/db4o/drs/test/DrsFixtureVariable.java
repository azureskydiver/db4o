/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class DrsFixtureVariable {
	
	private static final FixtureVariable _variable = new FixtureVariable("drs");
	
	public static DrsFixturePair value() {
		return (DrsFixturePair) _variable.value();
	}
	
	public static Object with(DrsFixturePair pair, Closure4 closure) {
		return _variable.with(pair, closure);
	}

}
