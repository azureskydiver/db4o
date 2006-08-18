/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import java.util.Hashtable;

import com.db4o.ObjectSet;
import com.db4o.YapStream;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;


public class DatabaseUnicityTest extends DrsTestCase {

	public void test() {
        Hashtable ht = new Hashtable();
        ExtObjectContainer oc = a().db();
        YapStream yapStream = ((YapStream) oc);
        yapStream.showInternalClasses(true);
        Query q = a().db().query();
        q.constrain(Db4oDatabase.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
        	Db4oDatabase d4b = (Db4oDatabase) objectSet.next();
        	Assert.isFalse(ht.containsKey(d4b.i_signature));
        	ht.put(d4b.i_signature, "");
        }
        yapStream.showInternalClasses(false);
        
        oc.close();
	}

}
