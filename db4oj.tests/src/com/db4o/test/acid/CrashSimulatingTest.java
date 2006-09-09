/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.acid;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;
import com.db4o.test.*;
import com.db4o.test.lib.*;


public class CrashSimulatingTest {
    
    
    public String _name;
    
    public CrashSimulatingTest _next;
    
    private static final String PATH = "TEMP/crashSimulate";
    private static final String FILE = PATH + "/cs";
    
    static final boolean LOG = false;
    
    
    public CrashSimulatingTest() {
    }
    
    public CrashSimulatingTest(CrashSimulatingTest next_, String name) {
        _next = next_;
        _name = name;
    }
    
    public void test() throws IOException{
    	
    	if (Test.isClientServer()) return;
        
        new File(FILE).delete();
        new File(PATH).mkdirs();
        
        createFile();
        
        CrashSimulatingIoAdapter adapterFactory = new CrashSimulatingIoAdapter(new RandomAccessFileAdapter());
        Db4o.configure().io(adapterFactory);

        ObjectContainer oc = Db4o.openFile(FILE);
        
        ObjectSet objectSet = oc.get(new CrashSimulatingTest(null, "three"));
        oc.delete(objectSet.next());
        
        oc.set(new CrashSimulatingTest(null, "four"));
        oc.set(new CrashSimulatingTest(null, "five"));
        oc.set(new CrashSimulatingTest(null, "six"));
        oc.set(new CrashSimulatingTest(null, "seven"));
        oc.set(new CrashSimulatingTest(null, "eight"));
        oc.set(new CrashSimulatingTest(null, "nine"));
        
        
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
                if(! stateAfterCommit(oc)){
                    Test.error();
                }
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
        ObjectSet objectSet = oc.query(CrashSimulatingTest.class);
        if(objectSet.size()!=names.length) {
            return false;
        }
        while(objectSet.hasNext()){
            CrashSimulatingTest cst = (CrashSimulatingTest)objectSet.next();
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
            oc.set(new SimplestPossible("delme"));
        }
        CrashSimulatingTest one = new CrashSimulatingTest(null, "one");
        CrashSimulatingTest two = new CrashSimulatingTest(one, "two");
        CrashSimulatingTest three = new CrashSimulatingTest(one, "three");
        oc.set(one);
        oc.set(two);
        oc.set(three);
        oc.commit();
        ObjectSet objectSet = oc.query(SimplestPossible.class);
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
