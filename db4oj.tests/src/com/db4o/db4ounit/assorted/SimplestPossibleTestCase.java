/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SimplestPossibleTestCase extends Db4oTestCase {
    
    public static void main(String[] args) {
        new SimplestPossibleTestCase().runSolo();
    }
    
    protected void store() {
        db().set(new SimplestPossibleItem("one"));
    }
    
    public void test(){
        Query q = db().query();
        q.constrain(SimplestPossibleItem.class);
        q.descend("name").constrain("one");
        ObjectSet objectSet = q.execute();
        SimplestPossibleItem item = (SimplestPossibleItem) objectSet.next();
        Assert.isNotNull(item);
        Assert.areEqual("one", item.getName());
    }

}
