/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.soda.ordered;
import com.db4o.query.*;


public class STOStringTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {

	public String foo;

    public STOStringTestCase() {
    }

    public STOStringTestCase(String str) {
        this.foo = str;
    }

    protected Object[] createData() {
        return new Object[] {
            new STOStringTestCase(null),
            new STOStringTestCase("bbb"),
            new STOStringTestCase("bbb"),
            new STOStringTestCase("dod"),
            new STOStringTestCase("aaa"),
            new STOStringTestCase("Xbb"),
            new STOStringTestCase("bbq")};
    }

    public void testAscending() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        q.descend("foo").orderAscending();
        
        expectOrdered(q, new int[] { 5, 4, 1, 2, 6, 3, 0 });
    }

    public void testDescending() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        q.descend("foo").orderDescending();
        
        expectOrdered(q, new int[] { 3, 6, 2, 1, 4, 5, 0 });
    }

    public void testAscendingLike() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        Query qStr = q.descend("foo");
        qStr.constrain("b").like();
        qStr.orderAscending();
        
        expectOrdered(q, new int[] { 5, 1, 2, 6 });
    }

    public void testDescendingContains() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        Query qStr = q.descend("foo");
        qStr.constrain("b").contains();
        qStr.orderDescending();
        
        expectOrdered(q, new int[] { 6, 2, 1, 5 });
    }
}
