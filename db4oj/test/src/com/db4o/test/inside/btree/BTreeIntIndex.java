/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.inside.btree;

import com.db4o.*;
import com.db4o.inside.btree.*;
import com.db4o.test.*;


public class BTreeIntIndex {
    
    public void test(){
        
        BTree btree = createBTree();
        
        addValues(btree);
        
        
        
        
    }
    
    private void addValues(BTree btree){
        
        Transaction trans = trans(); 
        
        for (int i = 0; i < values.length; i++) {
            btree.add(trans, new Integer(values[i]));
        }
        
    }
    
    private Transaction trans(){
        YapStream stream = (YapStream) Test.objectContainer();
        return stream.getTransaction();
    }
    
    private BTree createBTree(){
        YapStream stream = (YapStream) Test.objectContainer();
        Transaction systemTrans = stream.getSystemTransaction();
        
        BTree btree = new BTree(0, new YInt(stream), null);
        btree.write(systemTrans);
        
        return btree;
    }
    
    
    static int[] values = {3, 234, 55, 87, 2, 1, 101, 59, 70, 300, 288};
    
    static int[] sorted = {1, 2, 3, 55, 59, 70, 87, 101, 234, 288, 300};
    
    
    
    
    

}
