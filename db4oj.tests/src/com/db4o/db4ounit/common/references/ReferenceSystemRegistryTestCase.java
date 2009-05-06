/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.references;

import com.db4o.internal.*;

import db4ounit.*;


public class ReferenceSystemRegistryTestCase implements TestLifeCycle {

    private ReferenceSystemRegistry _registry;
    private ReferenceSystem _referenceSystem1;
    private ReferenceSystem _referenceSystem2;
    
    private static int TEST_ID = 5;

    public void setUp() throws Exception {
        _registry = new ReferenceSystemRegistry();
        _referenceSystem1 = new TransactionalReferenceSystem();
        _referenceSystem2 = new TransactionalReferenceSystem();
        _registry.addReferenceSystem(_referenceSystem1);
        _registry.addReferenceSystem(_referenceSystem2);
    }
    
    public void tearDown() throws Exception {
        
    }
    
    public void testRemoveId(){
        addTestReference();
        _registry.removeId(TEST_ID);
        assertTestReferenceNotPresent();
    }

    public void testRemoveNull(){
        _registry.removeObject(null);
    }

    public void testRemoveObject(){
        ObjectReference testReference = addTestReference();
        _registry.removeObject(testReference.getObject());
        assertTestReferenceNotPresent();
    }
    
    public void testRemoveReference(){
        ObjectReference testReference = addTestReference();
        _registry.removeReference(testReference);
        assertTestReferenceNotPresent();
    }
    
    public void testRemoveReferenceSystem(){
        addTestReference();
        _registry.removeReferenceSystem(_referenceSystem1);
        _registry.removeId(TEST_ID);
        Assert.isNotNull(_referenceSystem1.referenceForId(TEST_ID));
        Assert.isNull(_referenceSystem2.referenceForId(TEST_ID));
    }

    private void assertTestReferenceNotPresent() {
        Assert.isNull(_referenceSystem1.referenceForId(TEST_ID));
        Assert.isNull(_referenceSystem2.referenceForId(TEST_ID));
    }

    private ObjectReference addTestReference() {
        ObjectReference ref = new ObjectReference(TEST_ID);
        ref.setObject(new Object());
        _referenceSystem1.addExistingReference(ref);
        _referenceSystem2.addExistingReference(ref);
        return ref;
    }
    
    

}
