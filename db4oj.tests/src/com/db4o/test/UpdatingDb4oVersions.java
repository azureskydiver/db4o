/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.query.*;

/**
 * @decaf.ignore.jdk11
 */
public class UpdatingDb4oVersions {
    
    public static final String PATH = "./test/db4oVersions/";

    public static final String[] VERSIONS = {
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

    public List list;
    public Map map;
    public String name;
    
    public void configure(){
        Db4o.configure().allowVersionUpdates(true);
        Db4o.configure().objectClass(UpdatingDb4oVersions.class).objectField("name").indexed(true);
    }

    public void store(){
    	if(debugModeOrClientServer()){
    		return;
    	}
        String file = PATH + fileName();
        new File(file).mkdirs();
        new File(file).delete();
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        try {
	        UpdatingDb4oVersions udv = new UpdatingDb4oVersions();
	        udv.name = "check";
	        udv.list = objectContainer.collections().newLinkedList();
	        udv.map = objectContainer.collections().newHashMap(1);
	        objectContainer.store(udv);
	        udv.list.add("check");
	        udv.map.put("check","check");
        } finally {
        	objectContainer.close();
        }
    }
    
    public void test() throws IOException{
        if(debugModeOrClientServer()){
            return;
        }
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
    
    private boolean debugModeOrClientServer(){
    	return Deploy.debug || Test.isClientServer();  
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
        ObjectContainerBase container = (ObjectContainerBase)objectContainer;
        ClassMetadata classMetadata = container.classMetadataForName(this.getClass().getName());
        BTreeClassIndexStrategy btreeClassIndexStrategy = (BTreeClassIndexStrategy) classMetadata.index();
        BTree btree = btreeClassIndexStrategy.btree();
        Test.ensure(btree != null);
        int size = btree.size(container.transaction());
        Test.ensure(size == 1);
    }

    private void checkStoredObjectsArePresent(ExtObjectContainer objectContainer) {
        Query q = objectContainer.query();
        q.constrain(UpdatingDb4oVersions.class);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        UpdatingDb4oVersions udv = (UpdatingDb4oVersions)objectSet.next();
        Test.ensure(udv.name.equals("check"));
        Test.ensure(udv.list.size() == 1);
        Test.ensure(udv.list.get(0).equals("check"));
        Test.ensure(udv.map.get("check").equals("check"));
    }
    
    private static String fileName(){
        return Db4o.version().replace(' ', '_') + ".yap";
    }
}

