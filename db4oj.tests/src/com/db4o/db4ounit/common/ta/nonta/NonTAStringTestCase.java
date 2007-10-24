/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class NonTAStringTestCase extends NonTATestCaseBase implements OptOutCS{

	public static void main(String[] args) {
		new NonTAStringTestCase().runAll();
	}

    protected void assertValue(Object obj) {
        StringItem item = (StringItem) obj;
        Assert.areEqual("42", item.value());
        Assert.areEqual("hello", item.object());
    }

    protected Object createValue() {
        StringItem item = new StringItem();
        item.value = "42";
        item.obj = "hello";
        return item;
    }

}
