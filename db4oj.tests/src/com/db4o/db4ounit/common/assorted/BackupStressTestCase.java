/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;


public class BackupStressTestCase implements TestLifeCycle {
    
    private static boolean verbose = false;
    
    private static boolean runOnOldJDK = false;
    
    private static final String FILE = "backupstress.yap";
    
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
        stressTest.setUp();
        stressTest.test();
    }
    
    public void setUp(){
        Db4o.configure().objectClass(BackupStressItem.class).objectField("_iteration").indexed(true);
    }
    
    public void tearDown() {
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

	private void runTestIterations() {
		if(! runOnOldJDK && isOldJDK()) {
            System.out.println("BackupStressTest is too slow for regression testing on Java JDKs < 1.4");
            return;
        }
        
        BackupStressIteration iteration = new BackupStressIteration();
        _objectContainer.set(iteration);
        _objectContainer.commit();
        startBackupThread();
        for (int i = 1; i <= ITERATIONS; i++) {
            for (int obj = 0; obj < OBJECTS; obj++) {
                _objectContainer.set(new BackupStressItem("i" + obj, i));
                _commitCounter ++;
                if(_commitCounter >= COMMITS){
                    _objectContainer.commit();
                    _commitCounter = 0;
                }
            }
            iteration.setCount(i);
            _objectContainer.set(iteration);
            _objectContainer.commit();
        }
	}

	private void startBackupThread() {
		new Thread(new Runnable() {
			public void run() {
		        while(!_noMoreBackups){
		            _backups ++;
		            String fileName = backupFile(_backups);
		            deleteFile(fileName);
		            try {
		                _inBackup = true;
		                _objectContainer.ext().backup(fileName);
		                _inBackup = false;
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		}).start();
	}
   
    private void openDatabase(){
        deleteFile(FILE);
        _objectContainer = Db4o.openFile(FILE);
    }
    
    private void closeDatabase() throws InterruptedException{
        _noMoreBackups = true;
        while(_inBackup){
            Thread.sleep(1000);
        }
        _objectContainer.close();
    }
    
    private void checkBackups(){
        stdout("BackupStressTest");
        stdout("Backups created: " + _backups);
        
        for (int i = 1; i < _backups; i++) {
            stdout("Backup " + i);
            ObjectContainer container = Db4o.openFile(backupFile(i));
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
        System.out.println("BackupStressTest " + _backups + " files OK.");
        for (int i = 1; i <= _backups; i++) {
            deleteFile(backupFile(i));
        }
        deleteFile(FILE);
    }

	private boolean deleteFile(String fname) {
		return new File(fname).delete();
	}
    
    private boolean isOldJDK(){
        YapStream stream = (YapStream)_objectContainer;
        return stream.needsLockFileThread();
    }
    
    private String backupFile(int count){
        return "" + count + FILE;
    }

    private void stdout(String string) {
        if(verbose){
            System.out.println(string);
        }
    }


}
