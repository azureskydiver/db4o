/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * 
 */

public class GetUUIDTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new GetUUIDTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.generateUUIDs(ConfigScope.GLOBALLY);
    }
    
    protected void store() throws Exception {
        Item item = new Item("Item to be delete"); //$NON-NLS-1$
        store(item);
    }
    
    /*
     * Regression test for COR-546
     */
    public void testGetUUIDInCommittedCallbacks() {
        serverEventRegistry().committed().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                CommitEventArgs commitEventArgs = (CommitEventArgs) args;
                Iterator4 deletedObjectInfoCollection = commitEventArgs.deleted()
                        .iterator();
                while (deletedObjectInfoCollection.moveNext()) {
                    ObjectInfo objectInfo = (ObjectInfo) deletedObjectInfoCollection.current();
                    Assert.isNotNull(objectInfo.getUUID());
                }
            }
        });
        
        deleteAll(Item.class);
        db().commit();
    }

    public void testGetUUIDInCommittingCallbacks() {
        serverEventRegistry().committing().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                CommitEventArgs commitEventArgs = (CommitEventArgs) args;
                Iterator4 deletedObjectInfoCollection = commitEventArgs.deleted()
                        .iterator();
                while (deletedObjectInfoCollection.moveNext()) {
                    ObjectInfo objectInfo = (ObjectInfo) deletedObjectInfoCollection.current();
                    Assert.isNotNull(objectInfo.getUUID());
                }
            }
        });
        
        deleteAll(Item.class);
        db().commit();
    }
    
    public static class Item {
        public String _name;

        public Item(String name) {
            _name = name;
        }
        
        public String toString() {
            return _name;
        }
    }
}
