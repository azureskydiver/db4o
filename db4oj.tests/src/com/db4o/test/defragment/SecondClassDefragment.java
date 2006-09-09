/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.defragment;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;
import com.db4o.tools.*;


public class SecondClassDefragment {
    
    public static void store(){
        
        SCDSecondClass s1 = new SCDSecondClass("OK");
        SCDSecondClass s2 = new SCDSecondClass("OK");
        
        Test.store(new SCDSecondClass("Gone"));

        for (int i = 0; i < 2; i++) {
            Test.store(new SCDFirstClass(s1));
        }
        
        for (int i = 0; i < 3; i++) {
            Test.store(new SCDFirstClass(s2));
        }
    }
    
    public static void test(){
        Defragment.setSecondClass(new SCDSecondClass().getClass().getName());
        
        Test.defragment();
        
        Query q = Test.query();
        q.constrain(SCDSecondClass.class);
        ObjectSet objectSet = q.execute();
        
        Test.ensure(objectSet.size() == 2);
        
        while(objectSet.hasNext()){
            SCDSecondClass s = (SCDSecondClass)objectSet.next();
            Test.ensure(s._name.equals("OK"));
        }
        
        
    }

}
