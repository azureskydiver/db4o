/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.inside.btree.*;

import db4ounit.*;


public class BTreeSearchTestCase extends BTreeTestCaseBase{
    
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
        traverseKeysForDistinctValues(btree, values);
        
        btree.commit(trans());
        
        int id = btree.getID();
        
        stream().commit();
        
        reopen();
        
        btree = createIntKeyBTree(id);
        
        traverseKeysForDistinctValues(btree, values);
        
        return btree;
    }
    
    private void traverseKeysForDistinctValues(BTree btree, int[] values){
        int lastValue = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if(values[i] != lastValue){
                ExpectingVisitor expectingVisitor = createExpectingVisitor(values[i], occurences(values, values[i]));
                BTreeRange range = btree.search(trans(), new Integer(values[i]));
                range.traverseKeys(expectingVisitor);
                Assert.isTrue(expectingVisitor.allFound());
                lastValue = values[i];
            }
        }
    }
    
    private int occurences(int[] values, int value){
        int count = 0;
        for (int i = 0; i < values.length; i++) {
            if(values[i] == value){
                count ++;
            }
        }
        return count;
    }

    private ExpectingVisitor createExpectingVisitor(int value, int count) {
        int[] values = new int[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = value;
        }
        return new ExpectingVisitor(createExpectedValues(values));
    }

    private ExpectingVisitor createExpectingVisitor(int[] values) {
        return new ExpectingVisitor(createExpectedValues(values));
    }
    
    private Object[] createExpectedValues(int[] values) {
        Object[] ret = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = new Integer(values[i]);
        }
        return ret;
    }

}
