/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class NonTADateTestCase extends NonTATestCaseBase implements OptOutCS {

    public static Date first = new Date(1195401600000L);

    public static void main(String[] args) {
        new NonTADateTestCase().runAll();
    }

    protected void assertValue(Object obj) {
        DateItem item = (DateItem) obj;
        Assert.areEqual(first, item._untyped);
        Assert.areEqual(first, item._typed);
    }

    protected Object createValue() {
        DateItem item = new DateItem();
        item._typed = first;
        item._untyped = first;
        return item;
    }

    protected Class itemClass() {
        return DateItem.class;
    }

}
