/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;


public class FieldIndexTestCase extends FieldIndexTestCaseBase {
	
	private static final int[] BARS = new int[]{3,7,9,4};
    
    public static void main(String[] arguments) {
        new FieldIndexTestCase().runSolo();
    }
    
	public void store() {
		store(BARS);
	}
    
    public void testAllThere() throws Exception{
        for (int i = 0; i < BARS.length; i++) {
            Query q = createQuery(BARS[i]);
            ObjectSet objectSet = q.execute();
            Assert.areEqual(1, objectSet.size());
            FieldIndexItem fii = (FieldIndexItem) objectSet.next();
            Assert.areEqual(BARS[i], fii.bar);
        }
    }
    
}
