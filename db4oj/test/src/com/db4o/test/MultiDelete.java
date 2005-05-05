/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class MultiDelete {
    
    MultiDelete child;
    String name;
    Object forLong;
    Long myLong;
    Object[] untypedArr;
    Long[] typedArr;
    
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
        Db4o.configure().objectClass(this).cascadeOnUpdate(true);
    }
    
    public void store(){
        MultiDelete md = new MultiDelete();
        md.name = "killmefirst";
        md.setMembers();
        md.child = new MultiDelete();
        md.child.setMembers();
        Test.store(md);
    }
    
    private void setMembers(){
        forLong = new Long(100);
        myLong = new Long(100);
        untypedArr = new Object[]{
            new Long(10),
            "hi",
            new MultiDelete()
        };
        typedArr = new Long[]{
            new Long(3),
            new Long(7),
            new Long(9),
        };
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(MultiDelete.class);
        q.descend("name").constrain("killmefirst");
        ObjectSet objectSet = q.execute();
        MultiDelete md = (MultiDelete)objectSet.next();
        ExtObjectContainer oc = Test.objectContainer();
        long id = oc.getID(md);
        oc.delete(md);
        
        MultiDelete afterDelete = (MultiDelete)oc.getByID(id);
        
        oc.delete(md);
    }
    

}
