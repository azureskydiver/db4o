/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import java.io.*;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class FileSizeOnRollbackTestCase extends AbstractDb4oTestCase implements OptOutCS{

	public static void main(String[] args) {
		new FileSizeOnRollbackTestCase().runSolo();
	}
	
	public static class Item{
		public int _int; 
	}
	
	public void testFileSizeDoesNotIncrease(){
		
		storeSomeItems();
		produceSomeFreeSpace();
		
		int originalFileSize = fileSize();
		for (int i = 0; i < 100; i++) {
			store(new Item());
			db().rollback();
			Assert.areEqual(originalFileSize, fileSize());
		}
	}
	
	private void storeSomeItems(){
		for (int i = 0; i < 3; i++) {
			store(new Item());
		}
		db().commit();
	}
	
	private void produceSomeFreeSpace(){
		ObjectSet objectSet = newQuery(Item.class).execute();
		while(objectSet.hasNext()){
			db().delete(objectSet.next());
		}
		db().commit();
	}
	
	private int fileSize(){
        AbstractFileBasedDb4oFixture fixture = (AbstractFileBasedDb4oFixture) fixture();
        IoAdaptedObjectContainer container = (IoAdaptedObjectContainer) fixture().db();
        container.syncFiles();
        long length = new File(fixture.getAbsolutePath()).length();
        return (int)length;
	}
}
