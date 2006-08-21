/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.inside.btree.*;
import com.db4o.reflect.*;

import db4ounit.*;


public class FailingFieldIndexTestCase extends FieldIndexTestCaseBase {
	
	private static final int[] BARS = new int[]{3,7,9,4};
    
    public static void main(String[] arguments) {
        new FailingFieldIndexTestCase().runSolo();
    }
    
	public void store() {
		store(BARS);
	}
    
	public void testAccessingBTree() throws Exception{
    	
        YapStream stream = (YapStream)db();
        ReflectClass claxx = stream.reflector().forObject(new FieldIndexItem());
        YapClass yc = stream.getYapClass(claxx, false);
        YapField yf = yc.getYapField("bar");
        BTree bTree = yf.getIndex();
        
        Assert.isNotNull(bTree);
        expectKeysSearch(bTree, BARS);
    }
    
}
