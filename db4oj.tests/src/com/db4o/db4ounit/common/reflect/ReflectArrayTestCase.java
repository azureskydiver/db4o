/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.reflect;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class ReflectArrayTestCase extends AbstractDb4oTestCase {
	
	public void testNewInstance() {
		String[][] a23 = (String[][])newInstance(String.class, new int[] { 2, 3 });
		Assert.areEqual(2, a23.length);
		for (int i=0; i<a23.length; ++i) {
			Assert.areEqual(3, a23[i].length);
		}
	}

	private Object newInstance(Class elementType, int[] dimensions) {
		return reflector().array().newInstance(reflector().forClass(elementType), dimensions);
	}

}
