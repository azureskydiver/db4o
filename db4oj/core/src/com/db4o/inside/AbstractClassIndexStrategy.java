/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.YapClass;
import com.db4o.YapConst;

/**
 * @exclude
 */
public abstract class AbstractClassIndexStrategy implements ClassIndexStrategy {

	protected final YapClass _yapClass;

	public AbstractClassIndexStrategy(YapClass yapClass) {
		_yapClass = yapClass;
	}

	protected int yapClassID() {
		return _yapClass.getID();
	}

	public int ownLength() {
		return YapConst.YAPID_LENGTH;
	}

}