/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

/**
 * 
 */
public class IndexedByIdentity {
    
    Atom atom;
    
    static final int COUNT = 10;
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("atom").indexed(true);
    }
    
    public void store(){
        for (int i = 0; i < COUNT; i++) {
            IndexedByIdentity ibi = new IndexedByIdentity();
            ibi.atom = new Atom("ibi" + i);
            Test.store(ibi);
        } 
    }
    
    public void test(){
        
        for (int i = 0; i < COUNT; i++) {
            Query q = Test.query();
            q.constrain(Atom.class);
            q.descend("name").constrain("ibi" + i);
            ObjectSet objectSet = q.execute();
            Atom child = (Atom)objectSet.next();
            // child.name = "rünzelbrünft";
            q = Test.query();
            q.constrain(IndexedByIdentity.class);
            q.descend("atom").constrain(child).identity();
            objectSet = q.execute();
            Test.ensure(objectSet.size() == 1);
            IndexedByIdentity ibi = (IndexedByIdentity)objectSet.next();
            Test.ensure(ibi.atom == child);
        }
    }
    
}
