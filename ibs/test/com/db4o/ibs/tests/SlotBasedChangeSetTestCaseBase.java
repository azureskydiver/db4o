/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.ibs.*;
import com.db4o.ibs.engine.*;
import com.db4o.ibs.tests.mocking.*;

import db4ounit.*;
import db4ounit.extensions.*;


public abstract class SlotBasedChangeSetTestCaseBase extends AbstractDb4oTestCase{
    
    public static class Item {
        
        public String stringValue;
        
        public int intValue;
        
        public Item itemValue;
        
        public Item(String stringValue_, int intValue_) {
            stringValue = stringValue_;
            intValue = intValue_;
        }
    }
    
    public static class SubItem extends Item {
        
        public Integer integerValue;
        
        public SubItem(String stringValue_, int intValue_) {
            super(stringValue_, intValue_);
            integerValue = new Integer(intValue_);
        }       
    }
    
    final MockChangeSetListener listener = new MockChangeSetListener();
    
    final Item item = new Item("foo", 42);
    
    @Override
    protected void db4oSetupAfterStore() throws Exception {
        commitItem();
        setUpChangeSetPublisher();
    }
    
    @Override
    protected void configure(Configuration config) throws Exception {
        config.generateUUIDs(ConfigScope.GLOBALLY);
    }

    protected void commitItem() {
        commitItem(item);
    }
    
    protected void setUpChangeSetPublisher() {
        new ChangeSetPublisher(new SlotBasedChangeSetEngine(), listener).monitor(db());
    }

    protected void commitItem(Item i) {
        db().store(i);
        db().commit();
    }
    
    protected List<ChangeSet> changeSets() {
        return listener.changeSets();
    }
    
    protected void assertAreEqual(Db4oUUID expected, Db4oUUID actual){
        Assert.isNotNull(expected);
        Assert.areEqual(expected, actual);
    }
    
}
