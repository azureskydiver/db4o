/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;

public class ArrayMap4TATestCase extends TransparentActivationTestCaseBase {

    public static void main(String[] args) {
        new ArrayMap4TATestCase().runSolo();
    }

    protected void store() throws Exception {
        ArrayMap4<String, Integer> map = new ArrayMap4<String, Integer>();
        ArrayMap4Asserter.putData(map);
        store(map);
    }
    
    @SuppressWarnings("unchecked")
    private ArrayMap4<String, Integer> retrieveOnlyInstance() {
        return CollectionsUtil.retrieveMapFromDB(db(), reflector());
    }

    public void testClear() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertClear(map);
    }

    public void testClone() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertClone(map);
    }
    
    public void testContainsKey() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertContainsKey(map);
    }

    public void testContainsValue() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertContainsValue(map);
    }

    public void testEntrySet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertEntrySet(map);
    }

    public void testGet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertGet(map);
    }

    public void testIsEmpty() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertIsEmpty(map);
    }

    public void testKeySet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertKeySet(map);
    }

    public void testPut() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertPut(map);
    }

    public void testPutAll() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertPutAll(map);
    }

    public void testRemove_FromHead() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertRemove_FromHead(map);
    }

    public void testRemove_FromEnd() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertRemove_FromEnd(map);
    }

    public void testRemove_FromMiddle() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertRemove_FromMiddle(map);
    }
    
    public void testSize() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertSize(map);
    }

    public void testValues() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertValues(map);
    }

    public void testEquals() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertEquals(map);
    }
    
    public void testIncreaseSize() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4Asserter.assertIncreaseSize(map);
    }
}
