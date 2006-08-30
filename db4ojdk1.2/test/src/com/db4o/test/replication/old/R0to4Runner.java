/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.old;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class R0to4Runner {

    private ExtObjectContainer                _peerA;
    private ExtObjectContainer                _peerB;

    private static ReplicationConflictHandler _ignoreConflictHandler;

    private static final int                  LINKERS = 4;

    public void configure() {

        Db4o.configure().objectClass(R0.class).objectField("name").indexed(true);

        uUIDsOn(R0.class);
        uUIDsOn(R1.class);
        uUIDsOn(R2.class);
        uUIDsOn(R3.class);
        uUIDsOn(R4.class);

        _ignoreConflictHandler = new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess replicationProcess, Object a, Object b) {
                return null;
            }
        };
    }

    private void uUIDsOn(Class clazz) {
        Db4o.configure().objectClass(clazz).generateUUIDs(true);
        Db4o.configure().objectClass(clazz).generateVersionNumbers(true);
    }

    public void store() {
        _peerA = Test.objectContainer();

        R0Linker lCircles = new R0Linker();
        lCircles.setNames("circles");
        lCircles.linkCircles();
        lCircles.store(_peerA);

        R0Linker lList = new R0Linker();
        lList.setNames("list");
        lList.linkList();
        lList.store(_peerA);

        R0Linker lThis = new R0Linker();
        lThis.setNames("this");
        lThis.linkThis();
        lThis.store(_peerA);

        R0Linker lBack = new R0Linker();
        lBack.setNames("back");
        lBack.linkBack();
        lBack.store(_peerA);

    }

    public void test() {
        _peerA = Test.objectContainer();
        openReplica();

        ensureCount(_peerA, LINKERS);

        copyAllToB();
        replicateNoneModified();

        modifyR4(_peerA);

        openReplica();
        ensureR4Different();

        openReplica();
        replicateR4();

        openReplica();
        ensureR4Same();

    }

    private void openReplica() {
        _peerB = Test.replica();
    }

    private void ensureR4Different() {
        compareR4(_peerB, _peerA, false);
    }

    private void ensureR4Same() {
        compareR4(_peerB, _peerA, true);
        compareR4(_peerA, _peerB, true);
    }

    private void compareR4(ObjectContainer ocA, ObjectContainer ocB, boolean same) {
        Query q = ocA.query();
        q.constrain(R4.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            R4 r4 = (R4) objectSet.next();
            Query qb = ocB.query();
            qb.constrain(R4.class);
            qb.descend("name").constrain(r4.name);
            int expectedSize = same ? 1 : 0;
            int foundSize = qb.execute().size();
            Test.ensure(foundSize == expectedSize);
            if (foundSize != expectedSize) {
                System.out.println(foundSize);
            }
        }
    }

    private void modifyR4(ObjectContainer oc) {
        Query q = oc.query();
        q.constrain(R4.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            R4 r4 = (R4) objectSet.next();
            r4.name = r4.name + "_";
            oc.set(r4);
        }
        oc.commit();
    }

    private void copyAllToB() {
        Test.ensure(replicateAll(false) == LINKERS * 5);
    }

    private void replicateNoneModified() {
        Test.ensure(replicateAll() == 0);
    }

    private int replicateAll() {
        return replicateAll(true);
    }

    private void replicateR4() {
        Test.ensure(replicateAll(true) == LINKERS);
    }

    private int replicateAll(boolean modifiedOnly) {
        Collection4 allR0 = new Collection4();
        ReplicationProcess replication = _peerA.replicationBegin(_peerB, _ignoreConflictHandler);
        Query q = _peerA.query();
        q.constrain(R0.class);
        if (modifiedOnly) {
            replication.whereModified(q);
        }
        ObjectSet objectSet = q.execute();
        int replicated = 0;
        while (objectSet.hasNext()) {
            R0 r0 = (R0)objectSet.next();
            allR0.add(r0);
            replication.replicate(r0);
            replicated++;
        }
        replication.commit();
        ensureCount(_peerA, LINKERS);
        ensureCount(_peerB, LINKERS);
        Iterator4 i = allR0.iterator();
        while(i.moveNext()){
            R0 r0 = (R0)i.current();
            ObjectInfo infoA = _peerA.getObjectInfo(r0);
            ObjectInfo infoB = _peerB.getObjectInfo(r0);
            Db4oUUID uuidA = infoA.getUUID();
            Db4oUUID uuidB = infoB.getUUID();
            Test.ensure(uuidA.getLongPart() == uuidB.getLongPart());
            byte[] sigA = uuidA.getSignaturePart();
            byte[] sigB = uuidB.getSignaturePart();
            Test.ensure(Arrays.equals(sigA, sigB));
        }
        
        // reopen replication file
        _peerB = Test.replica();
        
        i = allR0.iterator();
        while(i.moveNext()){
            R0 r0 = (R0)i.current();
            ObjectInfo infoA = _peerA.getObjectInfo(r0);
            ObjectInfo infoB = _peerB.getObjectInfo(r0);
            Db4oUUID uuidA = infoA.getUUID();
            R0 r0B = (R0) _peerB.getByUUID(uuidA);
            Test.ensure(r0B != null);
            _peerB.activate(r0B, 1);
            Test.ensure(r0B.name.equals(r0.name));
        }
        
        return replicated;
    }

    private void ensureCount(ObjectContainer oc, int linkers) {
        ensureCount(oc, R0.class, linkers * 5);
        ensureCount(oc, R1.class, linkers * 4);
        ensureCount(oc, R2.class, linkers * 3);
        ensureCount(oc, R3.class, linkers * 2);
        ensureCount(oc, R4.class, linkers * 1);
    }

    private void ensureCount(ObjectContainer oc, Class clazz, int count) {
        Query q = oc.query();
        q.constrain(clazz);
        Test.ensure(q.execute().size() == count);
    }

}
