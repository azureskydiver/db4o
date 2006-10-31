/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import java.io.IOException;

import com.db4o.*;
import com.db4o.db4ounit.common.assorted.SimplestPossibleItem;
import com.db4o.db4ounit.util.*;
import com.db4o.io.RandomAccessFileAdapter;

import db4ounit.*;
import db4ounit.extensions.fixtures.OptOutCS;


public class CrashSimulatingTestCase implements TestCase, OptOutCS {	
    
    public String _name;
    
    public CrashSimulatingTestCase _next;
    
    private static final String PATH = Path4.combine(Path4.getTempPath(), "crashSimulate");
    private static final String FILE = Path4.combine(PATH, "cs");
    
    static final boolean LOG = false;
    
    
    public CrashSimulatingTestCase() {
    }
    
    public CrashSimulatingTestCase(CrashSimulatingTestCase next_, String name) {
        _next = next_;
        _name = name;
    }
    
    public void test() throws IOException{
    	
    	File4.delete(FILE);
    	File4.mkdirs(PATH);
        
        createFile();
        
        CrashSimulatingIoAdapter adapterFactory = new CrashSimulatingIoAdapter(new RandomAccessFileAdapter());
        Db4o.configure().io(adapterFactory);

        ObjectContainer oc = Db4o.openFile(FILE);
        
        ObjectSet objectSet = oc.get(new CrashSimulatingTestCase(null, "three"));
        oc.delete(objectSet.next());
        
        oc.set(new CrashSimulatingTestCase(null, "four"));
        oc.set(new CrashSimulatingTestCase(null, "five"));
        oc.set(new CrashSimulatingTestCase(null, "six"));
        oc.set(new CrashSimulatingTestCase(null, "seven"));
        oc.set(new CrashSimulatingTestCase(null, "eight"));
        oc.set(new CrashSimulatingTestCase(null, "nine"));
        
        
        oc.commit();
        oc.close();

        Db4o.configure().io(new RandomAccessFileAdapter());

        int count = adapterFactory.batch.writeVersions(FILE);

        checkFiles("R", adapterFactory.batch.numSyncs());
        checkFiles("W", count);
                
        System.out.println("Total versions: " + count);        
    }

	private void checkFiles(String infix,int count) {
        for (int i = 1; i <= count; i++) {
            if(LOG){
                System.out.println("Checking " + infix + i);
            }
            String fileName = FILE + infix + i;
            ObjectContainer oc = Db4o.openFile(fileName);
            if(! stateBeforeCommit(oc)){
                Assert.isTrue(stateAfterCommit(oc));
            }
            oc.close();
        }
    }
    
    private boolean stateBeforeCommit(ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "three"});
    }
    
    private boolean stateAfterCommit (ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "four", "five", "six", "seven", "eight", "nine"});
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
    
    private void createFile(){
        ObjectContainer oc = Db4o.openFile(FILE);
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
        File4.copy(FILE, FILE + "0");
    }
    
    
    
  public String toString() {
	return _name+" -> "+_next;
}  
    
    

}
