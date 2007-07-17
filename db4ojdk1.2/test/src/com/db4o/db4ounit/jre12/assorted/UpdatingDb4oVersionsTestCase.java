/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.assorted;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.query.*;
import com.db4o.test.*;

import db4ounit.*;


public class UpdatingDb4oVersionsTestCase implements TestLifeCycle {

	public static void main(String[] args) {
		new TestRunner(UpdatingDb4oVersionsTestCase.class).run();
	}
	
    static final String PATH = "./test/db4oVersions/";

    static final String[] VERSIONS = {
        "db4o_3.0.3", 
        "db4o_4.0.004", 
        "db4o_4.1.001", 
        "db4o_4.6.003",
        "db4o_4.6.004",
        "db4o_5.0.007",
        "db4o_5.1.001",
        "db4o_5.2.001", 
        "db4o_5.2.003",
        "db4o_5.2.008",
        "db4o_5.3.001", 
        "db4o_5.4.004",
        "db4o_5.5.2",
        "db4o_5.6.2",
        "db4o_6.2.001",
        "db4o_6.2.301"
    };

    List list;
    Map map;
    String name;
    
    public void configure(){
        Db4o.configure().allowVersionUpdates(true);
        Db4o.configure().objectClass(UpdatingDb4oVersions.class).objectField("name").indexed(true);
    }

    public void setUp() throws Exception {
    	configure();
        String file = PATH + fileName();
        new File(file).mkdirs();
        new File(file).delete();
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        try {
	        UpdatingDb4oVersions udv = new UpdatingDb4oVersions();
	        udv.name = "check";
	        udv.list = objectContainer.collections().newLinkedList();
	        udv.map = objectContainer.collections().newHashMap(1);
	        objectContainer.set(udv);
	        udv.list.add("check");
	        udv.map.put("check","check");
        } finally {
        	objectContainer.close();
        }
    }
    
    public void test() throws IOException{
        for(int i = 0; i < VERSIONS.length; i ++){
            String oldFilePath = PATH + VERSIONS[i];
            File oldFile = new File(oldFilePath);
            if(oldFile.exists()){
                
                String testFilePath = PATH + VERSIONS[i] + ".yap";
                new File(testFilePath).delete();
                
                File4.copy(oldFilePath, testFilePath);
                
                
                checkDatabaseFile(testFilePath);
                
                // Twice, to ensure everything is fine after opening, converting and closing.
                checkDatabaseFile(testFilePath);
                
                
            }else{
                System.err.println("Version upgrade check failed. File not found:");
                System.err.println(oldFile);
            }
        }
    }
    
    private void checkDatabaseFile(String testFile) {
        ExtObjectContainer objectContainer = Db4o.openFile(testFile).ext();
        try {
	        checkStoredObjectsArePresent(objectContainer);
	        checkBTreeSize(objectContainer);
        } finally {
        	objectContainer.close();
        }
    }

    private void checkBTreeSize(ExtObjectContainer objectContainer) {
        ObjectContainerBase yapStream = (ObjectContainerBase)objectContainer;
        StoredClass storedClass = objectContainer.storedClass(UpdatingDb4oVersions.class.getName());
        ClassMetadata yc = (ClassMetadata) storedClass;
        BTreeClassIndexStrategy btreeClassIndexStrategy = (BTreeClassIndexStrategy) yc.index();
        BTree btree = btreeClassIndexStrategy.btree();
        Assert.isNotNull(btree);
        int size = btree.size(yapStream.getTransaction());
        Assert.areEqual(1, size);
    }

    private void checkStoredObjectsArePresent(ExtObjectContainer objectContainer) {
        Query q = objectContainer.query();
        q.constrain(UpdatingDb4oVersions.class);
        ObjectSet objectSet = q.execute();
        Assert.areEqual(1, objectSet.size());
        UpdatingDb4oVersions udv = (UpdatingDb4oVersions)objectSet.next();
        Assert.areEqual("check", udv.name);
        Assert.areEqual(1, udv.list.size());
        Assert.areEqual("check", udv.list.get(0));
        Assert.areEqual("check", udv.map.get("check"));
    }
    
    private static String fileName(){
        return Db4o.version().replace(' ', '_') + ".yap";
    }

	public void tearDown() throws Exception {
	}
}

