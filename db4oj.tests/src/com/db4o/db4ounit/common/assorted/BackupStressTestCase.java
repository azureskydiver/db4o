/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class BackupStressTestCase implements Db4oTestCase, TestLifeCycle {
    
    private static boolean verbose = false;
    
    private static boolean runOnOldJDK = false;
    
    private static final String FILE = Path4.getTempFileName();
    
    private static final int ITERATIONS = 5;
    
    private static final int OBJECTS = 50;
    
    private static final int COMMITS = 10;
    
    private ObjectContainer _objectContainer;
    
    private volatile boolean _inBackup;
    
    private volatile boolean _noMoreBackups;
    
    private int _backups;
    
    private int _commitCounter;
    
    
    public static void main(String[] args) throws Exception {
        
        verbose = true;
        runOnOldJDK = true;
        
        BackupStressTestCase stressTest = new BackupStressTestCase();
        try {
			stressTest.setUp();
			stressTest.test();
		} finally {
			stressTest.tearDown();
		}
    }
    
    public void setUp() throws Exception{
    	deleteFile(FILE);
    }
    
    public void tearDown() throws IOException {
    	deleteFile(FILE);
    }

    public void test() throws Exception {
    	openDatabase();
    	try {        
    		runTestIterations();
    	} finally {
    		closeDatabase();
    	}
        checkBackups();
    }

	private void runTestIterations() throws Exception {
		if(! runOnOldJDK && isOldJDK()) {
            System.out.println("BackupStressTest is too slow for regression testing on Java JDKs < 1.4");
            return;
        }
        
        BackupStressIteration iteration = new BackupStressIteration();
        _objectContainer.store(iteration);
        _objectContainer.commit();
        Thread backupThread = startBackupThread();
        for (int i = 1; i <= ITERATIONS; i++) {
            for (int obj = 0; obj < OBJECTS; obj++) {
                _objectContainer.store(new BackupStressItem("i" + obj, i));
                _commitCounter ++;
                if(_commitCounter >= COMMITS){
                    _objectContainer.commit();
                    _commitCounter = 0;
                }
            }
            iteration.setCount(i);
            _objectContainer.store(iteration);
            _objectContainer.commit();
        }
        _noMoreBackups = true;
        backupThread.join();
	}

	private Thread startBackupThread() {
		Thread thread = new Thread(new Runnable() {
					public void run() {
				        while(!_noMoreBackups){
				            _backups ++;
				            String fileName = backupFile(_backups);
				            deleteFile(fileName);
							_inBackup = true;
							_objectContainer.ext().backup(fileName);
							_inBackup = false;		            
				        }
				    }
				});
		thread.start();
		return thread;
	}
   
    private void openDatabase(){
        deleteFile(FILE);
        _objectContainer = Db4o.openFile(config(), FILE);
    }
    
    private void closeDatabase() throws InterruptedException{
        while(_inBackup){
            Thread.sleep(1000);
        }
        _objectContainer.close();
    }
    
    private void checkBackups() throws IOException{
        stdout("BackupStressTest");
        stdout("Backups created: " + _backups);
        
        for (int i = 1; i < _backups; i++) {
            stdout("Backup " + i);
            ObjectContainer container = Db4o.openFile(config(), backupFile(i));
            try {
	            stdout("Open successful");
	            Query q = container.query();
	            q.constrain(BackupStressIteration.class);
	            BackupStressIteration iteration = (BackupStressIteration) q.execute().next();
	            
	            int iterations = iteration.getCount();
	            
	            stdout("Iterations in backup: " + iterations);
	            
	            if(iterations > 0){
	                q = container.query();
	                q.constrain(BackupStressItem.class);
	                q.descend("_iteration").constrain(new Integer(iteration.getCount()));
	                ObjectSet items = q.execute();
	                Assert.areEqual(OBJECTS, items.size());
	                while(items.hasNext()){
	                    BackupStressItem item = (BackupStressItem) items.next();
	                    Assert.areEqual(iterations, item._iteration);
	                }
	            }
            } finally {            
            	container.close();
            }
            stdout("Backup OK");
        }
        stdout("BackupStressTest " + _backups + " files OK.");
        for (int i = 1; i <= _backups; i++) {
            deleteFile(backupFile(i));
        }
    }

	private void deleteFile(String fname) {
		File4.delete(fname);
	}
    
    private boolean isOldJDK(){
        ObjectContainerBase stream = (ObjectContainerBase)_objectContainer;
        return stream.needsLockFileThread();
    }
    
    private String backupFile(int count){
        return FILE + count;
    }

    private void stdout(String string) {
        if(verbose){
            System.out.println(string);
        }
    }

	private Configuration config() {
		Configuration config = Db4o.newConfiguration();
        config.objectClass(BackupStressItem.class).objectField("_iteration").indexed(true);
        config.reflectWith(Platform4.reflectorForType(BackupStressItem.class));
        return config;
	}

}
