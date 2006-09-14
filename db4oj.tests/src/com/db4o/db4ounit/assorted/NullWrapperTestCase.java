/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class NullWrapperTestCase extends AbstractDb4oTestCase {
    
    static final int USE_AS_NULL = -9999;
    
    final int[] VALUES = new int[] {1, 2, USE_AS_NULL, 5, USE_AS_NULL, 7, USE_AS_NULL}; 
    
    public static void main(String[] args) {
        new NullWrapperTestCase().runSolo();
    }
    
    protected void configure() {
        Db4o.configure().objectClass(NullWrapperItem.class).objectField(NullWrapperItem.INTEGER_FIELDNAME).indexed(true);
    }
    
    public void test() throws Exception{
        for (int i = 0; i < VALUES.length; i++) {
            Integer integer = VALUES[i] == USE_AS_NULL ? null : new Integer(VALUES[i]);
            db().set(new NullWrapperItem(integer));
        }
        reopen();
        
        Query q = newQuery();
        q.constrain(NullWrapperItem.class);
        q.descend(NullWrapperItem.INTEGER_FIELDNAME).constrain(null);
        Assert.areEqual(3, q.execute().size());
    }

}
