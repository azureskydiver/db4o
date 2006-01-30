/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.util.Hashtable;

import com.db4o.ObjectSet;
import com.db4o.YapStream;
import com.db4o.ext.Db4oDatabase;
import com.db4o.query.Query;
import com.db4o.test.Test;

public class ReplicationFeatures {

    public void test() {
    	//All replication logic was deleted because all cases are covered by ReplicationFeaturesMain test.
        ensureDb4oDatabaseUnicity();
    }

    private void ensureDb4oDatabaseUnicity() {
    	System.out.println("This has to be moved to the test of the db4o implementation of ReplicationProvider and called after some replications have ocurred.");
        Hashtable ht = new Hashtable();
        YapStream yapStream = ((YapStream) Test.objectContainer());
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
    }

}