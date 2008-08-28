/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * Regression test case for COR-1117
 */
public class CallbackTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new CallbackTestCase().runAll();
    }

    public void testBaseClass() {
        runTest(new Item());
    }
    
    public void testDerived() {
    	runTest(new DerivedItem());
    }

	private void runTest(Item item) {
		store(item);
        db().commit();
        Assert.isTrue(item.isStored());
        Assert.isTrue(db().ext().isStored(item));
	}

    public static class Item {

        public transient ObjectContainer _objectContainer;

        public void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }

        public boolean isStored() {
            return _objectContainer.ext().isStored(this);
        }
    }
    
    public static class DerivedItem extends Item {
    }
}
