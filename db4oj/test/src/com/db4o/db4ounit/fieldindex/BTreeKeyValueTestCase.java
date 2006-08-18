/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.inside.btree.*;


/**
 * @exclude
 */
public class BTreeKeyValueTestCase extends BTreeTestCaseBase {
    
    private int[] keys = new int[]{6,5,5,5,3};
    
    private int[] values = new int[]{3,2,3,1,1};
    
    private int[] expectedValues = new int[] {1, 1, 2, 3, 3}; 
    
    public static void main(String[] arguments) {
        new BTreeKeyValueTestCase().runSolo();
    }
    
    public void test(){
        BTree bTree = new BTree(trans(), 0, new YInt(stream()), new YInt(stream()));
        for (int i = 0; i < keys.length; i++) {
            bTree.add(trans(), new Integer(keys[i]), new Integer(values[i]));
        }
        expectKeysSearch(bTree, keys);
        ExpectingVisitor expectingVisitor = new ExpectingVisitor(createExpectedValues(expectedValues), true);
        bTree.traverseValues(trans(), expectingVisitor);
        expectingVisitor.assertExpectations();
    }

}
