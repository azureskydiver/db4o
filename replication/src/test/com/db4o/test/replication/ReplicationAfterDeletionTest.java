/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;


public abstract class ReplicationAfterDeletionTest extends ReplicationTestcase{
    
    public void test(){
        init();

        deleteAndReplicate();
        deleteAndReplicate();
    }
    
    private void deleteAndReplicate() {
        delete(new Class[]{SPCChild.class, SPCParent.class});
        
        SPCChild child = new SPCChild("c1");
        SPCParent parent = new SPCParent(child, "p1");
        _providerA.storeNew(parent);
        _providerA.commit();
        
        replicateAll(_providerA, _providerB);
    }

}
