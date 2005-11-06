/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.conjunctions;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class CJSameField {
    
    private static final int USED = -9999;
    
    public int _id;
    
    public CJSameField(){
    }
    
    public CJSameField(int id){
        _id = id;
    }
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("_id").indexed(true);
    }
    
    public void store(){
        Test.store(new CJSameField(1));
        Test.store(new CJSameField(2));
        Test.store(new CJSameField(3));
        Test.store(new CJSameField(3));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(this.getClass());
        Query qId = q.descend("_id");
        qId.constrain(new Integer(1)).greater();
        qId.constrain(new Integer(2)).smaller().equal();
        expect(q, new int[]{2});
        
    }
    
    private void expect(Query q, int[] vals){
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            CJSameField cjs = (CJSameField)objectSet.next();
            boolean found = false;
            for (int i = 0; i < vals.length; i++) {
                if(cjs._id == vals[i]){
                    found = true;
                    vals[i] = USED;
                }
            }
            Test.ensure(found);
        }
        for (int i = 0; i < vals.length; i++) {
            Test.ensure(vals[i] == USED);
        }
    }
    
    
    
    
    
    

}
