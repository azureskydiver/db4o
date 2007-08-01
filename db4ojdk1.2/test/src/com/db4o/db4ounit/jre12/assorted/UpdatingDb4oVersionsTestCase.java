/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.test.*;

import db4ounit.*;


public class UpdatingDb4oVersionsTestCase implements TestLifeCycle {

	public static void main(String[] args) {
		new TestRunner(UpdatingDb4oVersionsTestCase.class).run();
	}

	private Configuration _config;

    public void configure(){
    	_config = Db4o.newConfiguration();
        _config.allowVersionUpdates(true);
        _config.objectClass(UpdatingDb4oVersions.class).objectField("name").indexed(true);
    }

    public void setUp() throws Exception {
    	configure();
        String file = UpdatingDb4oVersions.PATH + fileName();
        new File(file).mkdirs();
        new File(file).delete();
        ExtObjectContainer objectContainer = Db4o.openFile(_config, file).ext();
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
        for(int i = 0; i < UpdatingDb4oVersions.VERSIONS.length; i ++){
            String oldFilePath = UpdatingDb4oVersions.PATH + UpdatingDb4oVersions.VERSIONS[i];
            File oldFile = new File(oldFilePath);
            if(oldFile.exists()){
                String testFilePath = oldFilePath + ".yap";
                File4.delete(testFilePath);                
                File4.copy(oldFilePath, testFilePath);
                checkDatabaseFile(testFilePath);
                // Twice, to ensure everything is fine after opening, converting and closing.
                checkDatabaseFile(testFilePath);
            }else{
                Assert.fail("Version upgrade check failed. File not found:" + oldFile);
            }
        }
    }
    
    private void checkDatabaseFile(String testFile) {
        ExtObjectContainer objectContainer = Db4o.openFile(_config, testFile).ext();
        try {
	        checkStoredObjectsArePresent(objectContainer);
	        checkBTreeSize(objectContainer);
        } finally {
        	objectContainer.close();
        }
    }

    private void checkBTreeSize(ExtObjectContainer objectContainer) {
        ObjectContainerBase container = (ObjectContainerBase)objectContainer;
        Reflector reflector = container.reflector();
        ReflectClass claxx = reflector.forClass(UpdatingDb4oVersions.class);
        ClassMetadata yc = container.classMetadataForReflectClass(claxx); 
        BTreeClassIndexStrategy btreeClassIndexStrategy = (BTreeClassIndexStrategy) yc.index();
        BTree btree = btreeClassIndexStrategy.btree();
        Assert.isNotNull(btree);
        int size = btree.size(container.getTransaction());
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

