/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;


/**
 * @exclude
 * @sharpen.partial
 * @sharpen.ignore
 */
public abstract class YapStream extends YapStreamBase implements ExtObjectContainer {
	
	public YapStream(Configuration config,YapStream a_parent) {
		super(config,a_parent);
	}
}