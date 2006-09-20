/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.YapStream;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.Test;

import java.util.Hashtable;

public class DatabaseUnicityTest {

	public void test() {
        Hashtable ht = new Hashtable();
        ExtObjectContainer oc = Test.objectContainer();
        YapStream yapStream = ((YapStream) oc);
        yapStream.showInternalClasses(true);
        Query q = Test.query();
        q.constrain(Db4oDatabase.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
        	Db4oDatabase d4b = (Db4oDatabase) objectSet.next();
        	Test.ensure(!ht.containsKey(d4b.i_signature));
        	ht.put(d4b.i_signature, "");
        }
        yapStream.showInternalClasses(false);
        
        oc.close();
	}


}