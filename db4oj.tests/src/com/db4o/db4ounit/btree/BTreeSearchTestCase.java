/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import com.db4o.*;
import com.db4o.db4ounit.foundation.IntArrays4;
import com.db4o.inside.btree.*;

import db4ounit.extensions.Db4oTestCase;


public class BTreeSearchTestCase extends Db4oTestCase {
    
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
        BTree btree = BTreeAssert.createIntKeyBTree(stream(), 0);
        for (int i = 0; i < 5; i++) {
            btree = cycleIntKeys(btree, values);    
        }
    }
    
    private BTree cycleIntKeys(BTree btree, int[] values) throws Exception{
        for (int i = 0; i < values.length; i++) {
            btree.add(trans(), new Integer(values[i]));
        }
        expectKeysSearch(trans(), btree, values);
        
        btree.commit(trans());
        
        int id = btree.getID();
        
        stream().commit();
        
        reopen();
        
        btree = BTreeAssert.createIntKeyBTree(stream(), id);
        
        expectKeysSearch(trans(), btree, values);
        
        for (int i = 0; i < values.length; i++) {
            btree.remove(trans(), new Integer(values[i]));
        }
        
        BTreeAssert.assertEmpty(trans(), btree);
        
        btree.commit(trans());
        
        BTreeAssert.assertEmpty(trans(), btree);
        
        return btree;
    }

	private void expectKeysSearch(Transaction trans, BTree btree, int[] keys) {
	    int lastValue = Integer.MIN_VALUE;
	    for (int i = 0; i < keys.length; i++) {
	        if(keys[i] != lastValue){
	            ExpectingVisitor expectingVisitor = BTreeAssert.createExpectingVisitor(keys[i], IntArrays4.occurences(keys, keys[i]));
	            BTreeRange range = btree.search(trans, new Integer(keys[i]));
	            BTreeAssert.traverseKeys(range, expectingVisitor);
	            expectingVisitor.assertExpectations();
	            lastValue = keys[i];
	        }
	    }
	}
}
