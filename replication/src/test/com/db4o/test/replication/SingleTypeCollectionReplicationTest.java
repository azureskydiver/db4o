/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ObjectState;
import com.db4o.replication.ReplicationEvent;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

import java.util.Map;

public class SingleTypeCollectionReplicationTest extends ReplicationTestcase {

    protected void actualTest() {
		CollectionHolder h1 = new CollectionHolder();
        h1.map.put("1", "one");
        h1.set.add("two");
        h1.list.add("three");
        
        _providerA.storeNew(h1);
        _providerA.activate(h1);
        
		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB);
        
        final ObjectSet objectSet = _providerA.objectsChangedSinceLastReplication();
        
        while (objectSet.hasNext()) {
        	replication.replicate(objectSet.next());
        }
        
        replication.commit();
        
        ObjectSet it = _providerB.getStoredObjects(CollectionHolder.class);
        Test.ensure(it.hasNext());
        
        CollectionHolder replica = (CollectionHolder) it.next();
        Test.ensureEquals("one", replica.map.get("1"));
        Test.ensure(replica.set.contains("two"));
        Test.ensureEquals("three", replica.list.get(0));
	}

	protected void clean() {
		delete(new Class[]{CollectionHolder.class, Map.class});
	}

    public void test() {
        super.test();
    }
}
