/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;


public class BackupStressTest implements Runnable{
    
    
    private static final String FILE = "backupstress.yap";
    
    private static final int ITERATIONS = 10;
    
    private static final int OBJECTS = 100;
    
    private static final int COMMITS = 10;
    
    private ObjectContainer _objectContainer;
    
    private volatile boolean _backupsStarted;
    
    private volatile boolean _inBackup;
    
    private volatile boolean _noMoreBackups;
    
    private int _backups;
    
    private int _commitCounter;
    
    
    public static void main(String[] args) throws Exception {
        new BackupStressTest().test();
    }
    

    public void test() throws Exception {
        openDatabase();
        BackupStressIteration iteration = new BackupStressIteration();
        for (int i = 0; i < ITERATIONS; i++) {
            for (int obj = 0; obj < OBJECTS; obj++) {
                _objectContainer.set(new BackupStressItem("i" + obj, i));
                _commitCounter ++;
                if(_commitCounter >= COMMITS){
                    _objectContainer.commit();
                    _commitCounter = 0;
                }
            }
            iteration.setIteration(i);
            _objectContainer.set(iteration);
            _objectContainer.commit();
            if(! _backupsStarted){
                _backupsStarted = true;
                new Thread(this).start();
            }
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
        System.out.println("BackupStressTest");
        System.out.println("Backups created: " + _backups);
        
        for (int i = 1; i < _backups; i++) {
            System.out.println("Backup " + i);
            _objectContainer = Db4o.openFile(backupFile(i));
            System.out.println("Open successful");
            Query q = _objectContainer.query();
            q.constrain(BackupStressIteration.class);
            BackupStressIteration iteration = (BackupStressIteration) q.execute().next();
            System.out.println("Iterations in backup: " + iteration.getIteration());
            _objectContainer.close();
            System.out.println("Backup OK");
        }
    }
    

    /**
     * for backup thread
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
    
    private String backupFile(int count){
        return "" + count + FILE;
    }
    

}
