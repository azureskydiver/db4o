/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.dataobjects.*;


public class DataObjectReflection {
    
    static int specialAtomCount;
    
    static final String NAME = "backuptest";
    
    public void store(){
        Test.store(new Atom(NAME));
        Test.commit();
        
        Query q = Test.query();
        q.constrain(Atom.class);
        q.descend("name").constrain(NAME);
        specialAtomCount = q.execute().size();
    }
    
    public void test() {
        Db4o.configure().reflectWith(new DataObjectReflector());

        Query q = Test.query();
        q.constrain(Atom.class);
        q.descend("name").constrain(NAME);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == specialAtomCount);
        Atom atom = (Atom) objectSet.next();
        Test.ensure(atom.name.equals(NAME));
    }
    
    
}
