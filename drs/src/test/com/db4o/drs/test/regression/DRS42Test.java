package com.db4o.drs.test.regression;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.DrsTestCase;

import db4ounit.Assert;

/**
 * @sharpen.ignore
 */
public class DRS42Test extends DrsTestCase {

    NewPilot andrew = new NewPilot("Andrew", 100,
            new int[] { 100, 200, 300 });
    
    public void test() {
        storeToProviderA();
        replicateAllToProviderB();
    }

    void storeToProviderA() {
        TestableReplicationProviderInside provider = a().provider();
        provider.storeNew(andrew);
        provider.commit();
        ensureContent(andrew, provider);
    }

    void replicateAllToProviderB() {
        replicateAll(a().provider(), b().provider());
        ensureContent(andrew, b().provider());
    }

    private void ensureContent(NewPilot newPilot,
            TestableReplicationProviderInside provider) {
        ObjectSet result = provider.getStoredObjects(NewPilot.class);
        Assert.areEqual(1, result.size());

        NewPilot p = (NewPilot) result.next();
        Assert.areEqual(newPilot.getName(), p.getName());
        Assert.areEqual(newPilot.getPoints(), p.getPoints());
        for (int j = 0; j < newPilot.getArr().length; j++) {
            Assert.areEqual(newPilot.getArr()[j], p.getArr()[j]);
        }
    }
}
