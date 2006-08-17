/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.inside.btree.BTree;


public class BTreeSearchTestCase extends BTreeTestCaseBase{
    
    public static void main(String[] arguments) {
        new BTreeSearchTestCase().runSolo();
    }
    
    public void test() throws Exception{
        cycleIntKeys(new int[] { 3, 5, 5, 5, 7, 10, 11, 12, 12, 14});
        cycleIntKeys(new int[] { 3, 5, 5, 5, 5, 7, 10, 11, 12, 12, 14});
        cycleIntKeys(new int[] { 3, 5, 5, 5, 5, 5, 7, 10, 11, 12, 12, 14});
        cycleIntKeys(new int[] { 3, 3, 5, 5, 5, 7, 10, 11, 12, 12, 14, 14});
        cycleIntKeys(new int[] { 3, 3, 3, 5, 5, 5, 7, 10, 11, 12, 12, 14, 14, 14});
    }
    
    private void cycleIntKeys(int[] values) throws Exception{
        BTree btree = createIntKeyBTree(0);
        for (int i = 0; i < 5; i++) {
            btree = cycleIntKeys(btree, values);    
        }
    }
    
    private BTree cycleIntKeys(BTree btree, int[] values) throws Exception{
        for (int i = 0; i < values.length; i++) {
            btree.add(trans(), new Integer(values[i]));
        }
        expectKeysSearch(btree, values);
        
        btree.commit(trans());
        
        int id = btree.getID();
        
        stream().commit();
        
        reopen();
        
        btree = createIntKeyBTree(id);
        
        expectKeysSearch(btree, values);
        
        for (int i = 0; i < values.length; i++) {
            btree.remove(trans(), new Integer(values[i]));
        }
        
        assertEmpty(trans(), btree);
        
        btree.commit(trans());
        
        assertEmpty(trans(), btree);
        
        return btree;
    }

}
