/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.Arrays4;

import db4ounit.*;

/**
 * @exclude
 */
public class Arrays4TestCase implements TestCase {

	public void testContainsInstanceOf() {
		Object[] array = new Object[] { "foo", new Integer(42) };
		Assert.isTrue(Arrays4.containsInstanceOf(array, String.class));
		Assert.isTrue(Arrays4.containsInstanceOf(array, Integer.class));
		Assert.isTrue(Arrays4.containsInstanceOf(array, Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(array, Float.class));
		
		Assert.isFalse(Arrays4.containsInstanceOf(new Object[0], Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(new Object[1], Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(null, Object.class));
	}
}
