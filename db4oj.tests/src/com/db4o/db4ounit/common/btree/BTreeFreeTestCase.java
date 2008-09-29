/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class BTreeFreeTestCase extends BTreeTestCaseBase {

    private static final int[] VALUES = new int[] { 1, 2, 5, 7, 8, 9, 12 };

    public static void main(String[] args) {
        new BTreeFreeTestCase().runSolo();
    }
    
    public void test() throws Throwable{
        add(VALUES);
        BTreeAssert.assertAllSlotsFreed(fileTransaction(), _btree, new CodeBlock() {
			public void run() throws Throwable {
		        _btree.free(systemTrans());
		        systemTrans().commit();
			}
		});
        
    }

	private LocalTransaction fileTransaction() {
		return ((LocalTransaction)trans());
	}

}
