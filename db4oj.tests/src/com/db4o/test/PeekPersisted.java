/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class PeekPersisted {
    
    public String name;
    
    public PeekPersisted child;
    
    public void storeOne(){
        PeekPersisted current = this;
        current.name = "1";
        for (int i = 2; i < 11; i++) {
            current.child = new PeekPersisted();
            current.child.name = "" + i;
            current = current.child;
        }
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(PeekPersisted.class);
        q.descend("name").constrain("1");
        ObjectSet objectSet = q.execute();
        PeekPersisted pp = (PeekPersisted)objectSet.next();
        for (int i = 0; i < 10; i++) {
            peek(pp, i);
        }
    }
    
    private void peek(PeekPersisted original, int depth){
        ExtObjectContainer oc = Test.objectContainer();
        PeekPersisted peeked = (PeekPersisted )oc.peekPersisted(original, depth, true);
        Test.ensure(peeked != null);
        Test.ensure(! oc.isStored(peeked));
        for (int i = 0; i <= depth; i++) {
            Test.ensure(peeked != null);
            Test.ensure(! oc.isStored(peeked));
            peeked = peeked.child;
        }
        Test.ensure(peeked == null);
    }

}
