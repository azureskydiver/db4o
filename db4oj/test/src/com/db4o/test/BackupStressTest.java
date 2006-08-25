/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;


public class BackupStressTest implements Runnable{
    
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
        
        BackupStressTest stressTest = new BackupStressTest();
        stressTest.configure();
        stressTest.test();
    }
    
    public void configure(){
        Db4o.configure().objectClass(BackupStressItem.class).objectField("_iteration").indexed(true);
    }

    public void test() throws Exception {
        if(Test.isClientServer()){
            // running once in SOLO is enough
            return;
        }
        
        openDatabase();
        if(! runOnOldJDK){
            if(usesLockFileThread()){
                System.out.println("BackupStressTest is too slow for regression testing on Java JDKs < 1.4");
                closeDatabase();
                return;
            }
        }
        
        BackupStressIteration iteration = new BackupStressIteration();
        _objectContainer.set(iteration);
        _objectContainer.commit();
        new Thread(this).start();
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
        closeDatabase();
        checkBackups();
    }
   
    private void openDatabase(){
        new File(FILE).delete();
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
            _objectContainer = Db4o.openFile(backupFile(i));
            stdout("Open successful");
            Query q = _objectContainer.query();
            q.constrain(BackupStressIteration.class);
            BackupStressIteration iteration = (BackupStressIteration) q.execute().next();
            
            int iterations = iteration.getCount();
            
            stdout("Iterations in backup: " + iterations);
            
            if(iterations > 0){
                q = _objectContainer.query();
                q.constrain(BackupStressItem.class);
                q.descend("_iteration").constrain(new Integer(iteration.getCount()));
                ObjectSet items = q.execute();
                Test.ensure(items.size() == OBJECTS);
                while(items.hasNext()){
                    BackupStressItem item = (BackupStressItem) items.next();
                    Test.ensure(item._iteration == iterations);
                }
            }
            
            _objectContainer.close();
            stdout("Backup OK");
        }
        System.out.println("BackupStressTest " + _backups + " files OK.");
        for (int i = 1; i <= _backups; i++) {
            new File(backupFile(i)).delete();
        }
        new File(FILE).delete();
    }


    /**
     * for the backup thread
     */
    public void run() {
        while(!_noMoreBackups){
            _backups ++;
            String fileName = backupFile(_backups);
            new File(fileName).delete();
            try {
                _inBackup = true;
                _objectContainer.ext().backup(fileName);
                _inBackup = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean usesLockFileThread(){
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
