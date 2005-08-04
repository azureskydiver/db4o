/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;


public class ReadAs {
    
    public String name;
    
    public void storeOne(){
        name = "zuluhu";
    }
    
    public void test(){
        
        if(! Test.isClientServer()){
        
            Db4o.configure().objectClass(this).readAs(ReadAsRead.class);
            
            Test.reOpen();
            
            Query q = Test.query();
            q.constrain(ReadAsRead.class);
            ObjectSet objectSet = q.execute();
            Test.ensure(objectSet.size() == 1);
            ReadAsRead rar = (ReadAsRead)objectSet.next();
            Test.ensure(rar.name.equals("zuluhu"));
        }
        
    }


}
