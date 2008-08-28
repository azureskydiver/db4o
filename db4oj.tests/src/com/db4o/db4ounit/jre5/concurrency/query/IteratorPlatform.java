/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.concurrency.query;

import java.util.*;

/**
 * @sharpen.ignore
 * @decaf.ignore
 */
public class IteratorPlatform {

	static Object next(final Iterator result) {
		return result.next();
	}

}
