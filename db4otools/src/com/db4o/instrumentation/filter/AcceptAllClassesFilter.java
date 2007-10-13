/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.filter;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;

/**
 * @exclude
 */
public class AcceptAllClassesFilter implements ClassFilter {

	public boolean accept(Class clazz) {
		return !BloatUtil.isPlatformClassName(clazz.getName());
	}

}
