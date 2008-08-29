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
    
    public void testPublicCallback() {
    	runTest(new PublicCallback());
    }

    public void testPrivateCallback() {
        runTest(new PrivateCallback());
    }
    
    public void testPackageCallback() {
    	runTest(new PackageCallback());
    }
    
    public void testInheritedPublicCallback() {
    	runTest(new InheritedPublicCallback());
    }

    public void testInheritedPrivateCallback() {
        runTest(new InheritedPrivateCallback());
    }
    
    public void testInheritedPackageCallback() {
    	runTest(new InheritedPackageCallback());
    }

	private void runTest(Item item) {
		store(item);
        db().commit();
        Assert.isTrue(item.isStored());
        Assert.isTrue(db().ext().isStored(item));
	}
	
	public static class Item {
		public transient ObjectContainer _objectContainer;

		public boolean isStored() {
            return _objectContainer.ext().isStored(this);
        }
	}

    public static class PackageCallback extends Item {
        void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class InheritedPackageCallback extends PackageCallback {
    }
    
    public static class PrivateCallback extends Item {
        @SuppressWarnings("unused")
		private void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class InheritedPrivateCallback extends PrivateCallback {
    }
    
    public static class PublicCallback extends Item {
        public void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class InheritedPublicCallback extends PublicCallback {
    }
}
