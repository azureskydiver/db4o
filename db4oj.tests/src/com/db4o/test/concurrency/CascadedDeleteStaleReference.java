/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.concurrency;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.*;


/**
 * @decaf.ignore.jdk11
 */
public class CascadedDeleteStaleReference {
    
    List list;
    CDSReference ref;
    
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
        Db4o.configure().objectClass(this).cascadeOnUpdate(true);
    }
    
    public void storeOne(){
        list = Test.objectContainer().collections().newLinkedList();
        ref = new CDSReference();
        list.add(ref);
    }
    
    public void testOne(){
        
        
        if(Test.isClientServer()){
            
            
            ExtObjectContainer serverOC = Test.server().ext().objectContainer().ext();
            
            Query q = serverOC.query();
            q.constrain(CDSReference.class);
            ObjectSet objectSet = q.execute();
            CDSReference willbeStale = (CDSReference) objectSet.next();
            
            
            Test.delete(this);
            serverOC.delete(this);
            Test.commit();
            serverOC.commit();
            
            
            serverOC.purge(willbeStale);
            
            // serverOC.close();
            
        }
    }
    
    
    public static class CDSReference{
    }
}
