/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.test.*;


public class BTreeIntIndex {
    
    public void test(){
        if(Test.isClientServer()){
            return;
        }
        
        BTree btree = new BTree(trans(), 0, new YInt(stream()), null);
        
        for (int i = 0; i < 5; i++) {
            btree = cycle(btree);    
        }
        
    }
    
    private BTree cycle(BTree btree){
        addValues(btree);
        expect(btree, SORTED);
        
        btree.commit(trans());
        
        expect(btree, SORTED);
        
        removeValues(btree);
        
        expect(btree, REMOVED);
        
        btree.rollback(trans());
        
        expect(btree, SORTED);
        
        int id = btree.getID();
        Test.reOpen();
        
        btree = new BTree(trans(), id, new YInt(stream()), null);
        
        expect(btree, SORTED);
        
        removeValues(btree);
        
        expect(btree, REMOVED);
        
        btree.commit(trans());
        
        expect(btree, REMOVED);
        
        // remove all but 1
        for (int i = 1; i < REMOVED.length; i++) {
            btree.remove(trans(), new Integer(REMOVED[i]));
        }
        
        expect(btree, ONE);
        
        btree.commit(trans());
        
        expect(btree, ONE);
        
        btree.remove(trans(), new Integer(1));
        
        btree.rollback(trans());
        
        expect(btree, ONE);
        
        btree.remove(trans(), new Integer(1));
        
        btree.commit(trans());
        
        expect(btree, NONE);
        
        return btree;
        
    }
    
    
    
    private void removeValues(BTree btree){
        btree.remove(trans(), new Integer(3));
        btree.remove(trans(), new Integer(101));
    }
    
    
    private void addValues(BTree btree){
        Transaction trans = trans(); 
        for (int i = 0; i < VALUES.length; i++) {
            btree.add(trans, new Integer(VALUES[i]));
        }
    }
    
    private void expect(BTree btree, final int[] values){
        final int[] cursor = new int[] {0};
        btree.traverseKeys(trans(), new Visitor4() {
            public void visit(Object obj) {
                // System.out.println(obj);
                Test.ensure(((Integer)obj).intValue() == values[cursor[0]]);
                cursor[0] ++;
            }
        });
        Test.ensure(cursor[0] == values.length);
    }
    
    private YapStream stream(){
        return (YapStream) Test.objectContainer();
    }
    
    private Transaction trans(){
        return stream().getTransaction();
    }
    
    private Transaction systemTrans(){
        return stream().getSystemTransaction();
    }
    
    
    static final int[] VALUES = {3, 234, 55, 87, 2, 1, 101, 59, 70, 300, 288};
    
    static final int[] SORTED = {1, 2, 3, 55, 59, 70, 87, 101, 234, 288, 300};
    
    static final int[] REMOVED = {1, 2, 55, 59, 70, 87, 234, 288, 300};
    
    static final int[] ONE = {1};
    
    static final int[] NONE = {};
    
    
    

}
