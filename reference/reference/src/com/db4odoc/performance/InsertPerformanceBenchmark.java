/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4odoc.performance;

import java.io.File;
import java.util.ArrayList;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.Configuration;
import com.db4o.io.MemoryIoAdapter;


public class InsertPerformanceBenchmark {
    
    private static int _count = 100000;
    
    private static int _commitInterval = 10000;
    
    private static int _depth = 3;
    
    private static boolean _isClientServer = false;
    
    private static boolean TCP = true;
    
    private static String _filePath = "performance.db4o";
    
    private static String _host = "localhost";
	
    private static final int PORT = 4477;
    
    
    private ObjectContainer objectContainer;
    
    private ObjectServer objectServer;
    
    
    private long startTime;
    
    
    public static void main(String[] arguments) {
    	//new InsertPerformanceBenchmark().runDifferentObjectsTest();
    	//new InsertPerformanceBenchmark().runCommitTest();
    	//new InsertPerformanceBenchmark().runRamDiskTest();
    	//new InsertPerformanceBenchmark().runClientServerTest();
    	//new InsertPerformanceBenchmark().runIndexTest();
    	new InsertPerformanceBenchmark().runInheritanceTest();
    }
    // end main
    
    private void runCommitTest(){
    	
    	configureForCommitTest();
    	initForCommitTest();
    	
    	clean();
    	System.out.println("Storing objects as a bulk:");
    	open();
    	store();
    	close();
    	
    	clean();
    	System.out.println("Storing objects with commit after each " + _commitInterval + " objects:");
    	open();
    	storeWithCommit();
    	close();
    }
    // end runCommitTest

    private void runRamDiskTest(){
    	
    	configureRamDrive();
    	
    	initForHardDriveTest();
    	clean();
    	System.out.println("Storing " + _count + " objects of depth " + _depth + " on a hard drive:");
    	open();
    	store();
    	close();
    	
    	initForRamDriveTest();
    	clean();
    	System.out.println("Storing " + _count + " objects of depth " + _depth + " on a RAM disk:");
    	open();
    	store();
    	close();
    	
    }
    // end runRamDiskTest

    private void runClientServerTest(){
    	
    	configureClientServer();
    	
    	init();
    	clean();
    	System.out.println("Storing " + _count + " objects of depth " + _depth + " locally:");
    	open();
    	store();
    	close();
    	
    	initForClientServer();
    	clean();
    	System.out.println("Storing " + _count + " objects of depth " + _depth + " remotely:");
    	open();
    	store();
    	close();
    	
    }
    // end runClientServerTest

    private void runInheritanceTest(){
    	
    	configure();
    	init();
    	clean();
    	System.out.println("Storing " + _count + " objects of depth " + _depth);
    	open();
    	store();
    	close();
    	
    	clean();
    	System.out.println("Storing " + _count + " inherited objects of depth " + _depth);
    	open();
    	storeInherited();
    	close();
    	
    }
    // end runInheritanceTest

    private void runDifferentObjectsTest(){
    	
    	configure();
    	init();
    	System.out.println("Storing " + _count + " objects with " + _depth + " levels of embedded objects:");
    	
    	clean();
    	System.out.println(" - primitive object with int field");
    	open();
    	storeSimplest();
    	close();
    	
    	open();
    	System.out.println(" - object with String field");
    	store();
    	close();
    	
    	clean();
    	open();
    	System.out.println(" - object with StringBuffer field");
    	storeWithStringBuffer();
    	close();
    	
    	clean();
    	open();
    	System.out.println(" - object with int array field");
    	storeWithArray();
    	close();
    	
    	clean();
    	open();
    	System.out.println(" - object with ArrayList field");
    	storeWithArrayList();
    	close();
    	
    }
    // end runDifferentObjectsTest
    
    private void runIndexTest(){

    	init();
    	System.out.println("Storing " + _count + " objects with " + _depth + " levels of embedded objects:");
    	
    	clean();
    	configure();
    	System.out.println(" - no index");
    	open();
    	store();
    	close();
    	
    	configureIndex();
    	System.out.println(" - index on String field");
    	open();
    	store();
    	close();
    }
    // end runIndexTest
    
    private void init(){
    	_count = 10000;
        _depth = 3;
        _isClientServer = false;
        	
    }
    // end init
    
    private void initForClientServer(){
    	_count = 10000;
        _depth = 3;
        _isClientServer = true;
        _host = "localhost";	
    }
    // end initForClientServer
    
    private void initForRamDriveTest(){
    	_count = 30000;
        _depth = 3;
        _filePath = "r:\\performance.db4o";
        _isClientServer = false;
        	
    }
    // end initForRamDriveTest
    
    private void initForHardDriveTest(){
    	_count = 30000;
        _depth = 3;
        _filePath = "performance.db4o";
        _isClientServer = false;
        	
    }
    // end initForHardDriveTest
    
    private void initForCommitTest(){
    	_count = 100000;
    	_commitInterval = 10000;
        _depth = 3;
        _isClientServer = false;
        	
    }
    // end initForCommitTest
    
    private void clean(){
    	new File(_filePath).delete();
    }
    // end clean
    
    private void configure(){
    	Configuration config = Db4o.configure(); 
        config.lockDatabaseFile(false);
        config.weakReferences(false);
        config.io(new MemoryIoAdapter());
        config.flushFileBuffers(false);
    }
    // end configure

    private void configureForCommitTest(){
    	Configuration config = Db4o.configure(); 
        config.lockDatabaseFile(false);
        config.weakReferences(false);
        // flushFileBuffers should be set to true to ensure that
        // the commit information is physically written 
        // and in the correct order
        config.flushFileBuffers(true);
    }
    // end configureForCommitTest

    private void configureIndex(){
    	Configuration config = Db4o.configure(); 
        config.lockDatabaseFile(false);
        config.weakReferences(false);
        config.io(new MemoryIoAdapter());
        config.flushFileBuffers(false);
        config.objectClass(Item.class).objectField("_name").indexed(true);
    }
    // end configureIndex

    private void configureClientServer(){
    	Configuration config = Db4o.configure(); 
        config.lockDatabaseFile(false);
        config.weakReferences(false);
        config.flushFileBuffers(false);
        config.clientServer().singleThreadedClient(true);
    }
    // end configureClientServer

    private void configureRamDrive(){
    	Configuration config = Db4o.configure(); 
        config.lockDatabaseFile(false);
        config.weakReferences(false);
        config.flushFileBuffers(true);
    }
    // end configureRamDrive
    
    private void store(){
        startTimer();
        for (int i = 0; i < _count ;i++) {
            Item item = new Item("load", null);
            for (int j = 1; j < _depth; j++) {
                item = new Item("load", item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end store

    private void storeInherited(){
        startTimer();
        for (int i = 0; i < _count ;i++) {
            ItemDerived item = new ItemDerived("load", null);
            for (int j = 1; j < _depth; j++) {
                item = new ItemDerived("load", item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeInherited

    private void storeWithCommit(){
        startTimer();
        int k = 0;
        while (k < _count){
	        for (int i = 0; i < _commitInterval ;i++) {
	            Item item = new Item("load", null);
	            k++;
	            for (int j = 1; j < _depth; j++) {
	                item = new Item("load", item);
	            }
	            objectContainer.set(item);
	        }
	        objectContainer.commit();
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeWithCommit
    
    private void storeWithStringBuffer(){
        startTimer();
        for (int i = 0; i < _count ;i++) {
            ItemWithStringBuffer item = new ItemWithStringBuffer(new StringBuffer("load"), null);
            for (int j = 1; j < _depth; j++) {
                item = new ItemWithStringBuffer(new StringBuffer("load"), item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeWithStringBuffer
    
    private void storeSimplest(){
        startTimer();
        for (int i = 0; i < _count ;i++) {
        	SimplestItem item = new SimplestItem(i, null);
            for (int j = 1; j < _depth; j++) {
                item = new SimplestItem(i, item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeSimplest
    
    private void storeWithArray(){
        startTimer();
        int[] array = new int[]{1,2,3,4};
        for (int i = 0; i < _count ;i++) {
        	int[] id = new int[]{1,2,3,4};
        	ItemWithArray item = new ItemWithArray(id, null);
            for (int j = 1; j < _depth; j++) {
            	int[] id1 = new int[]{1,2,3,4};
                item = new ItemWithArray(id1, item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeWithArray
    
    private void storeWithArrayList(){
    	startTimer();
    	ArrayList idList = new ArrayList();
    	idList.add(1);
    	idList.add(2);
    	idList.add(3);
    	idList.add(4);
        for (int i = 0; i < _count ;i++) {
        	ArrayList ids = new ArrayList();
        	ids.addAll(idList);
        	ItemWithArrayList item = new ItemWithArrayList(ids, null);
            for (int j = 1; j < _depth; j++) {
            	ArrayList ids1 = new ArrayList();
            	ids1.addAll(idList);
            	item = new ItemWithArrayList(ids1, item);
            }
            objectContainer.set(item);
        }
        objectContainer.commit();
        stopTimer("Store "+ totalObjects() + " objects");
    }
    // end storeWithArrayList
    
    private int totalObjects(){
    	return _count * _depth;
    }
    // end totalObjects
    
    private void open(){
        if(_isClientServer){
        	int port = TCP ? PORT : 0;
        	String user = "db4o";
        	String password = user;
            objectServer = Db4o.openServer(_filePath, port);
            objectServer.grantAccess(user, password);
            objectContainer = TCP ? Db4o.openClient(_host, port, user,
					password) : objectServer.openClient();
        } else{
            objectContainer = Db4o.openFile(_filePath);
        }
    }
    // end open
    
    private void close(){
        objectContainer.close();
        if(_isClientServer){
            objectServer.close();
        }
    }
    //end close
    
    private void startTimer(){
    	startTime = System.currentTimeMillis();
    }
    // end startTimer
    
    private void stopTimer(String message){
        long stop = System.currentTimeMillis();
        long duration = stop - startTime;
        System.out.println(message + ": " + duration + "ms");
    }
    // end stopTimer
    
    public static class Item {
        
        public String _name;
        public Item _child;

        public Item(){
            
        }
        
        public Item(String name, Item child){
            _name = name;
            _child = child;
        }
    }
    // end Item
    
    public static class ItemDerived extends Item {
    	
    	public ItemDerived(String name, ItemDerived child){
            super(name, child);
        }
    }
    // end ItemDerived
    
    public static class ItemWithStringBuffer {
        
        public StringBuffer _name;
        public ItemWithStringBuffer _child;
        
        public ItemWithStringBuffer(){
        }
        
        public ItemWithStringBuffer(StringBuffer name, ItemWithStringBuffer child){
            _name = name;
            _child = child;
        }
    }
    // end ItemWithStringBuffer
    
    public static class SimplestItem {
        
        public int _id;
        public SimplestItem _child;

        public SimplestItem(){
        }
        
        public SimplestItem(int id, SimplestItem child){
            _id = id;
            _child = child;
        }
    }
    // end SimplestItem

    public static class ItemWithArray {
        
        public int[] _id;
        public ItemWithArray _child;
        
        public ItemWithArray(){
        }
        
        public ItemWithArray(int[] id, ItemWithArray child){
            _id = id;
            _child = child;
        }
    }
    // end ItemWithArray
    
    public static class ItemWithArrayList {
        
        public ArrayList _ids;
        public ItemWithArrayList _child;
        
        public ItemWithArrayList(){
        }
        
        public ItemWithArrayList(ArrayList ids, ItemWithArrayList child){
            _ids = ids;
            _child = child;
        }
    }
    // end ItemWithArrayList
}
