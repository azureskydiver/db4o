/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.filesize;

import db4ounit.*;


public class FileSizeTestCase extends FileSizeTestCaseBase {
    
    private static final int ITERATIONS = 100;

	public static void main(String[] args) {
		new FileSizeTestCase().runSolo();
	}
	
	public void testConsistentSizeOnRollback(){
		
		storeSomeItems();
		produceSomeFreeSpace();
		
		int originalFileSize = fileSize();
		for (int i = 0; i < ITERATIONS; i++) {
			store(new Item());
			db().rollback();
			Assert.areEqual(originalFileSize, fileSize());
		}
	}
    
    public void testConsistentSizeOnCommit(){
        storeSomeItems();
        db().commit();
        int originalFileSize = fileSize();
        for (int i = 0; i < ITERATIONS; i++) {
            db().commit();
            Assert.areEqual(originalFileSize, fileSize());
        }
    }
    
    public void testConsistentSizeOnUpdate(){
        storeSomeItems();
        produceSomeFreeSpace();
        Item item = new Item(); 
        store(item);
        db().commit();
        int originalFileSize = fileSize();
        for (int i = 0; i < ITERATIONS; i++) {
            store(item);
            db().commit();
        }
        Assert.areEqual(originalFileSize, fileSize());
    }
    
    public void testConsistentSizeOnReopen() throws Exception{
        db().commit();
        reopen();
        int originalFileSize = fileSize();
        for (int i = 0; i < ITERATIONS; i++) {
            reopen();
        }
        Assert.areEqual(originalFileSize, fileSize());
    }
    
    public void testConsistentSizeOnUpdateAndReopen() throws Exception{
        produceSomeFreeSpace();
        store(new Item());
        db().commit();
        int originalFileSize = fileSize();
        for (int i = 0; i < ITERATIONS; i++) {
            store(retrieveOnlyInstance(Item.class));
            db().commit();
            reopen();
        }
        Assert.areEqual(originalFileSize, fileSize());
    }


}
