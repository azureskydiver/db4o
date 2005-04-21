/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.constraints;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class UniqueField {
    
    int id;
    String name;
    
    public void store(){
        UniqueField uf = new UniqueField();
        uf.name = "Mark I";
        uf.id = 1;
        Test.store(uf);
        uf = new UniqueField();
        uf.name = "Mark II";
        uf.id = 2;
        Test.store(uf);
        check();
    }
    
    public void test(){
        UniqueField uf = new UniqueField();
        uf.name = "Mark I";
        Test.store(uf);
        check();
        Test.commit();
        check();
        
        Query q = Test.query();
        q.constrain(UniqueField.class);
        q.descend("id").constrain(new Integer(1));
        uf = (UniqueField) q.execute().next();
        uf.name = "Mark II";
        Test.store(uf);
        check();
        Test.commit();
        check();
    }
    
    private void check(){
        ObjectContainer oc = Test.objectContainer();
        Query q = Test.query();
        q.constrain(UniqueField.class);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 2);
        while(objectSet.hasNext()){
            UniqueField uf = (UniqueField)objectSet.next();
            oc.ext().refresh(uf, Integer.MAX_VALUE);
            if(uf.id == 1){
                Test.ensure(uf.name.equals("Mark I"));
            }else{
                Test.ensure(uf.name.equals("Mark II"));
            }
        }
    }
    
    public boolean objectCanNew(ObjectContainer objectContainer){
        return checkConstraints(objectContainer);
    }

    public boolean objectCanUpdate(ObjectContainer objectContainer){
        return checkConstraints(objectContainer);
    }
    
    private boolean checkConstraints(ObjectContainer objectContainer){
        Query q = objectContainer.query();
        q.constrain(UniqueField.class);
        q.descend("name").constrain(name);
        ObjectSet objectSet = q.execute(); 
        int size = objectSet.size();
        if(size == 0){
            return true;
        }
        if(size > 1){
            return false;
        }
        UniqueField uf = (UniqueField)objectSet.next(); 
        return uf == this;
    }
    
}
