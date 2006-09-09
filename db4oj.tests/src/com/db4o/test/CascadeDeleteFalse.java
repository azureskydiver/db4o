/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;


/**
 * 
 */
public class CascadeDeleteFalse {
    
    public CascadeDeleteFalseHelper h1;
    public CascadeDeleteFalseHelper h2;
    public CascadeDeleteFalseHelper h3;
    
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
        Db4o.configure().objectClass(this).objectField("h3").cascadeOnDelete(false);
    }
    
    public void storeOne(){
        Test.deleteAllInstances(CascadeDeleteFalseHelper.class);
        h1 = new CascadeDeleteFalseHelper();
        h2 = new CascadeDeleteFalseHelper();
        h3 = new CascadeDeleteFalseHelper();
    }
    
    public void testOne(){
        checkHelperCount(3);
        Test.delete(this);
        checkHelperCount(1);
        
    }
    
    private void checkHelperCount (int count){
        Query q = Test.query();
        q.constrain(CascadeDeleteFalseHelper.class);
        Test.ensure(q.execute().size() == count);
    }
    
    
    
    public static class CascadeDeleteFalseHelper{
        
    }
}
