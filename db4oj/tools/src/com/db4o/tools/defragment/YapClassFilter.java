/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;

public interface YapClassFilter {
	boolean accept(YapClass yapClass);
}
