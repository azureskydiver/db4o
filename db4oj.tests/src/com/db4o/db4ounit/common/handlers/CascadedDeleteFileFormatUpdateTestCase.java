/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;

import db4ounit.*;

/**
 * @exclude
 */
public class CascadedDeleteFileFormatUpdateTestCase extends FormatMigrationTestCaseBase {
	
	private boolean _failed;
	
	protected void configureForStore(Configuration config) {
		config.objectClass(ParentItem.class).cascadeOnDelete(true);
	}
	
	protected void configureForTest(Configuration config) {
		configureForStore(config);
		config.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic d) {
				if(d instanceof DeletionFailed){
					// Can't assert directly here, db4o eats the exception. :/
					_failed = true;
				}
			}
		});
	}
	
	public static class ParentItem {

		public ChildItem[] _children;
		
		public static ParentItem newTestInstance(){
			ParentItem item = new ParentItem();
			item._children = new ChildItem[]{
				new ChildItem(),
				new ChildItem(),
			};
			return item;
		}
		
	}
	
	public static class ChildItem {
		
	}

	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		ParentItem parentItem = (ParentItem) retrieveInstance(objectContainer, ParentItem.class);
		Assert.isNotNull(parentItem._children);
		Assert.isNotNull(parentItem._children[0]);
		Assert.isNotNull(parentItem._children[1]);
		objectContainer.delete(parentItem);
		Assert.isFalse(_failed);
		store(objectContainer);
	}

	private Object retrieveInstance(ExtObjectContainer objectContainer,
			Class clazz) {
		return objectContainer.query(clazz).next();
	}

	protected String fileNamePrefix() {
		return "migrate_cascadedelete_" ;
	}

	protected void store(ExtObjectContainer objectContainer) {
		storeObject(objectContainer, ParentItem.newTestInstance());
	}

}
