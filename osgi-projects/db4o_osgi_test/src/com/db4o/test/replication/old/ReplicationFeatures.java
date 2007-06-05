/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.test.replication.old;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeatures {

    public String              name;

    public void configure() {
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }

    public void storeOne() {
    }

    public void testUuidGeneration() {
        ExtObjectContainer master = Test.objectContainer();

        //Test.ensure(false);
        //Db4oUUID uuid = master.getObjectInfo(masterObject).getUUID();
        //Test.ensure(masterObject == master.getByUUID(uuid));
    }

    public void testOne() {
        name = "rf1";
        Test.store(this);
        replicateAll();
        checkOne("rf1");

        name = "rf2";
        Test.store(this);
        Test.commit();
        replicateAll();

        checkOne("rf2");
        replicateAll();
        checkAllEqual();
        ensureDb4oDatabaseUnicity();
    }

    public void testContainerIdentity() {
        if (Test.isClientServer()) {
            ExtObjectContainer clientObjectContainer = Test.objectContainer();
            ExtObjectContainer serverObjectContainer =

            Test.server().ext().objectContainer().ext();

            Test.ensure(clientObjectContainer.identity().equals(serverObjectContainer.identity()));
        }
    }
	
	private void printVersion() {
		// System.out.println("Version: " + Test.objectContainer().getObjectInfo(this).getVersion());
	}

    private void replicateAll() {
        ExtObjectContainer peerA = Test.objectContainer();
        ExtObjectContainer peerB = Test.replica();
        final ReplicationProcess replication =

        peerA.ext().replicationBegin(peerB, new ReplicationConflictHandler() {

            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {

                // the object was change in both ObjectContainers since
                // the last replication

                return null;

            }

        });

        //      replication.setDirection(master, slave); //Default is bidirectional.

        Query q = peerA.query();
        q.constrain(ReplicationFeatures.class);
        replication.whereModified(q);

        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            Object masterObject = objectSet.next();
			
			((ReplicationFeatures)masterObject).printVersion();

            // check version numbers and decide upon direction,
            // depending which one changed after last synchronisation
            replication.replicate(masterObject);
            // replication.checkConflict(masterObject); // Another option (peek).
        }
        replication.commit();
        peerB.close();
    }

    private void checkAllEqual() {
        DeepCompare comparator = new DeepCompare();
        ExtObjectContainer master = Test.objectContainer();
        ExtObjectContainer slave = Test.replica();
        Query q = master.query();
        q.constrain(ReplicationFeatures.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            Object masterObject = objectSet.next();

            Db4oUUID uuid = master.getObjectInfo(masterObject).getUUID();
            Object slaveObject = slave.getByUUID(uuid);

            master.activate(masterObject, Integer.MAX_VALUE);
            slave.activate(slaveObject, Integer.MAX_VALUE);
            Test.ensure(comparator.isEqual(masterObject, slaveObject));
        }
        slave.close();
    }

    private void checkOne(String name) {
        ObjectContainer slave = Test.replica();
        Query q = slave.query();
        q.constrain(this.getClass());
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        ReplicationFeatures rf = (ReplicationFeatures) objectSet.next();
        Test.ensure(rf.name.equals(name));
        slave.close();
    }

    private void ensureDb4oDatabaseUnicity() {
        Hashtable ht = new Hashtable();
        ObjectContainerBase yapStream = ((ObjectContainerBase) Test.objectContainer());
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

    private void replicationSnippet() {

        // open any two ObjectContainers, local or Client/Server
        ExtObjectContainer peerA = Test.objectContainer();
        ExtObjectContainer peerB = Test.replica();

        // create a replication process with a ConflictHandler
        final ReplicationProcess replication = peerA.ext().replicationBegin(peerB,
            new ReplicationConflictHandler() {

                public Object resolveConflict(ReplicationProcess process, Object a,

                Object b) {
                    // the object was changed in both ObjectContainers since the
                    // last time the two ObjectContainers were replicated.

                    // return a or b to indicate which version you want to keep or
                    // null to replicate nothing
                    return null;
                }
            });

        //      You could set this replication process to one-directional,
        //      It is bidirectional by default.
        //      replication.setDirection(peerA, peerB);

        Query query = peerA.query();

        // You can do any query that you like here
        query.constrain(ReplicationFeatures.class);

        // This method adds a special constraint to query only for
        // objects modified since the last replication between the
        // two ObjectContainers.
        replication.whereModified(query);

        ObjectSet objectSet = query.execute();
        while (objectSet.hasNext()) {
            // checks version numbers and decides upon direction,
            // depending which one changed after last synchronisation
            replication.replicate(objectSet.next());
        }
        replication.commit();
        peerB.close();
    }

}