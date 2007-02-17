/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import java.io.IOException;

import com.db4o.*;
import com.db4o.db4ounit.common.assorted.SimplestPossibleItem;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.io.RandomAccessFileAdapter;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.OptOutCS;


public class CrashSimulatingTestCase implements TestCase, OptOutCS {	
    
    public String _name;
    
    public CrashSimulatingTestCase _next;
    
    
    static final boolean LOG = false;
    
    
    public CrashSimulatingTestCase() {
    }
    
    public CrashSimulatingTestCase(CrashSimulatingTestCase next_, String name) {
        _next = next_;
        _name = name;
    }
    
    private boolean hasLockFileThread(){
        if (!Platform4.hasLockFileThread()) {
            return false;
        }
        return ! Platform4.hasNio();
    }
    
    public void test() throws IOException{
    	if(hasLockFileThread()){
    		System.out.println("CrashSimulatingTestCase is ignored on platforms with lock file thread.");
    		return;
    	}
    	
        String path = Path4.combine(Path4.getTempPath(), "crashSimulate");
        String fileName = Path4.combine(path, "cs");
        
    	File4.delete(fileName);
    	File4.mkdirs(path);
        
        Db4o.configure().bTreeNodeSize(4);

        createFile(fileName);
        
        CrashSimulatingIoAdapter adapterFactory = new CrashSimulatingIoAdapter(new RandomAccessFileAdapter());
        Db4o.configure().io(adapterFactory);
        
        ObjectContainer oc = Db4o.openFile(fileName);
        
        ObjectSet objectSet = oc.get(new CrashSimulatingTestCase(null, "three"));
        oc.delete(objectSet.next());
        
        oc.set(new CrashSimulatingTestCase(null, "four"));
        oc.set(new CrashSimulatingTestCase(null, "five"));
        oc.set(new CrashSimulatingTestCase(null, "six"));
        oc.set(new CrashSimulatingTestCase(null, "seven"));
        oc.set(new CrashSimulatingTestCase(null, "eight"));
        oc.set(new CrashSimulatingTestCase(null, "nine"));
        oc.set(new CrashSimulatingTestCase(null, "10"));
        oc.set(new CrashSimulatingTestCase(null, "11"));
        oc.set(new CrashSimulatingTestCase(null, "12"));
        oc.set(new CrashSimulatingTestCase(null, "13"));
        oc.set(new CrashSimulatingTestCase(null, "14"));
        
        oc.commit();
        
        Query q = oc.query();
        q.constrain(CrashSimulatingTestCase.class);
        objectSet = q.execute();
        while(objectSet.hasNext()){
        	CrashSimulatingTestCase cst = (CrashSimulatingTestCase) objectSet.next();
            if( !  (cst._name.equals("10") || cst._name.equals("13")) ){
                oc.delete(cst);
            }
        }
        
        oc.commit();

        oc.close();

        Db4o.configure().io(new RandomAccessFileAdapter());

        int count = adapterFactory.batch.writeVersions(fileName);

        checkFiles(fileName, "R", adapterFactory.batch.numSyncs());
        checkFiles(fileName, "W", count);
                
        System.out.println("Total versions: " + count);        
    }

	private void checkFiles(String fileName, String infix,int count) {
        for (int i = 1; i <= count; i++) {
            if(LOG){
                System.out.println("Checking " + infix + i);
            }
            String versionedFileName = fileName + infix + i;
            ObjectContainer oc = Db4o.openFile(versionedFileName);
            
            if(! stateBeforeCommit(oc)){
                if(! stateAfterFirstCommit(oc)){
                    Assert.isTrue(stateAfterSecondCommit(oc));
                }
            }
            oc.close();
        }
    }
    
    private boolean stateBeforeCommit(ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "three"});
    }
    
    private boolean stateAfterFirstCommit (ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "four", "five", "six", "seven", "eight", "nine", "10", "11", "12", "13", "14" });
    }
    
    private boolean stateAfterSecondCommit (ObjectContainer oc){
        return expect(oc, new String[] {"10", "13"});
    }
    
    private boolean expect(ObjectContainer oc, String[] names){
        ObjectSet objectSet = oc.query(CrashSimulatingTestCase.class);
        if(objectSet.size()!=names.length) {
            return false;
        }
        while(objectSet.hasNext()){
            CrashSimulatingTestCase cst = (CrashSimulatingTestCase)objectSet.next();
            boolean found = false;
            for (int i = 0; i < names.length; i++) {
                if(cst._name.equals(names[i])){
                    names[i] = null;
                    found = true;
                    break;
                }
            }
            if(! found){
                return false;
            }
        }
        for (int i = 0; i < names.length; i++) {
            if(names[i] != null){
                return false;
            }
        }
        return true;
    }
    
    private void createFile(String fileName){
        ObjectContainer oc = Db4o.openFile(fileName);
        for (int i = 0; i < 10; i++) {
            oc.set(new SimplestPossibleItem("delme"));
        }
        CrashSimulatingTestCase one = new CrashSimulatingTestCase(null, "one");
        CrashSimulatingTestCase two = new CrashSimulatingTestCase(one, "two");
        CrashSimulatingTestCase three = new CrashSimulatingTestCase(one, "three");
        oc.set(one);
        oc.set(two);
        oc.set(three);
        oc.commit();
        ObjectSet objectSet = oc.query(SimplestPossibleItem.class);
        while(objectSet.hasNext()){
            oc.delete(objectSet.next());
        }
        oc.close();
        File4.copy(fileName, fileName + "0");
    }
    
	public String toString() {
		return _name+" -> "+_next;
	}
	
}
