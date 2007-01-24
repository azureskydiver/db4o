/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.util;

import com.db4o.foundation.io.Path4;

/**
 * @sharpen.ignore
 * @exclude
 */
public class IOServices {
	
	public static String buildTempPath(String fname) {
		return Path4.combine(Path4.getTempPath(), fname);
	}

}
