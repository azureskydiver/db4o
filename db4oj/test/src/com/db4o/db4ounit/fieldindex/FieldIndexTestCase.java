/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.inside.btree.BTree;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;


public class FieldIndexTestCase extends FieldIndexTestCaseBase {
    
    public static void main(String[] arguments) {
        new FieldIndexTestCase().runSolo();
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
