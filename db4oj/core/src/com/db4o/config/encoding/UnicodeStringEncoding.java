/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.encoding;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class UnicodeStringEncoding extends LatinStringEncoding {
	
	protected LatinStringIO createStringIo(StringEncoding encoding) {
		return new UnicodeStringIO();
	}

}
