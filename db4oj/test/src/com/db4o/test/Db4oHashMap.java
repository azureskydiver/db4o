/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.tools.*;
import com.db4o.types.*;


/**
 * 
 */
public class Db4oHashMap {
    
    public static class Db4oHashMapHelper{
        public Db4oHashMapHelper i_child;
        public List i_childList;
        
    }
    
    static final int COUNT = 10;
    
    static final String[] DEFAULT = {"wow", "cool", "great"};
    static final String MORE = "more and more ";
    
    Map i_map;
    Db4oHashMapHelper i_helper;
    
    public void storeOne(){
        i_map = Test.objectContainer().collections().newHashMap(10);
        setDefaultValues(i_map);
        i_helper = helper(10);
    }
    
    private static Db4oHashMapHelper helper(int a_depth){
        if(a_depth > 0){
            Db4oHashMapHelper helper = new Db4oHashMapHelper();
            helper.i_childList = Test.objectContainer().collections().newLinkedList();
            helper.i_childList.add("hi");
            helper.i_child = helper(a_depth - 1);
            return helper;
        }
        return null;
    }
    
    private void setDefaultValues(Map a_map){
        for (int i = 0; i < DEFAULT.length; i++) {
            a_map.put(DEFAULT[i], new Atom(DEFAULT[i]));
        }
    }
    
    public void testOne(){
        
        ObjectContainer oc = Test.objectContainer();
        
        checkHelper(i_helper);
        runElementTest(true);
        
        oc = Test.objectContainer();
        oc.set(this);
        oc.set(i_helper);
        oc.commit();
        
        checkHelper(i_helper);
        runElementTest(false);
        
        boolean defrag = true;
        
        
        if(!Test.clientServer  && defrag){
            long id = Test.objectContainer().getID(this);
            Test.close();
            new Defragment().run(AllTests.FILE_SOLO, true);
            Test.open();
            restoreMembers();
            checkHelper(i_helper);
            runElementTest(false);
        }
    }
    
    
    private void runElementTest(boolean onOriginal){
        
        Map otherMap = new HashMap();
        
        Atom atom = null;
        
        tDefaultValues();
        
        int itCount = 0;
        Iterator i = i_map.keySet().iterator();
        while(i.hasNext()){
            String str = (String)i.next();
            itCount ++;
            atom = (Atom)i_map.get(str);
            Test.ensure(atom.name.equals(str));
            otherMap.put(str, atom);
        }
        Test.ensure(itCount == DEFAULT.length);
        
        
        Test.ensure(i_map.size() == DEFAULT.length);
        Test.ensure(i_map.isEmpty() == false);
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        i_map.get("great");
        Test.ensure(((Atom)i_map.get("great")).name.equals("great"));
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        
        if(onOriginal){
	        Query q = Test.query();
	        Db4oHashMap template = new Db4oHashMap();
	        template.i_map = Test.objectContainer().collections().newHashMap(1);
	        template.i_map.put("cool", new Atom("cool"));
	        q.constrain(template);
	        ObjectSet qResult = q.execute();
	        Test.ensure(qResult.size() == 1);
	        Test.ensure(qResult.next() == this);
        }
        
        Test.ensure(i_map.keySet().containsAll(otherMap.keySet()));
        
        
        Object[] arr = i_map.keySet().toArray();
        tDefaultArray(arr);
        
        
        String[] cmp = new String[DEFAULT.length];
        System.arraycopy(DEFAULT,0, cmp, 0, DEFAULT.length);
        
        i = i_map.keySet().iterator();
        while(i.hasNext()){
            String str = (String)i.next();
            boolean found = false;
            for (int j = 0; j < cmp.length; j++) {
                if(str.equals(cmp[j])){
                    cmp[j] = null;
                    found = true;
                }
            }
            Test.ensure(found);
        }
        
        for (int j = 0; j < cmp.length; j++) {
            Test.ensure(cmp[j] == null);
        }
        
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        Test.ensure(i_map.isEmpty() == false);
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        i_map.put("yup", new Atom("yup"));
        
        Test.objectContainer().set(this);
        Test.objectContainer().set(this);
        Test.objectContainer().set(this);
        Test.objectContainer().set(i_map);
        Test.objectContainer().set(i_map);
        Test.objectContainer().set(i_map);
        Test.objectContainer().set(i_helper);
        Test.objectContainer().set(i_helper);
        Test.objectContainer().set(i_helper);
        Test.objectContainer().commit();
        
        Test.ensure(i_map.size() == 4);
        
        atom = (Atom)i_map.get("yup");
        Test.ensure(atom.name.equals("yup"));
        
        Atom removed = (Atom)i_map.remove("great");  
        
        Test.ensure(removed.name.equals("great"));
        Test.ensure(i_map.remove("great") == null);
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        Test.ensure(i_map.size() == 3);
        
        Test.ensure(i_map.keySet().removeAll(otherMap.keySet()));
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        Test.ensure(! i_map.keySet().removeAll(otherMap.keySet()));
        Test.ensure(i_map.size() == 1);
        i = i_map.keySet().iterator();
        String str = (String)i.next();
        Test.ensure(str.equals("yup"));
        Test.ensure(! i.hasNext());
        
        i_map.clear();
        Test.ensure(i_map.isEmpty());
        Test.ensure(i_map.size() == 0);

		setDefaultValues(i_map);
        
        String[] strArr = new String[1];
        strArr = (String[])i_map.keySet().toArray(strArr);
        tDefaultArray(strArr);
        
        i_map.clear();
        i_map.put("zero", "zero");

        long start = System.currentTimeMillis();
        
        for (int j = 0; j < COUNT; j++) {
            i_map.put(MORE + j, new Atom(MORE + j));
        }
        long stop = System.currentTimeMillis();
        // System.out.println("Time to put " + COUNT + " elements: " + (stop - start) + "ms");
        Test.ensure(i_map.size() == COUNT + 1);
        lookupLast();
        
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        // System.out.println("Deactivated");
        lookupLast();
        // System.out.println("Activated");
        lookupLast();
        
        Test.reOpen();
        restoreMembers();
        // System.out.println("Reopened");
        lookupLast();
        
        atom = new Atom("double");
        
        i_map.put("double", atom);
        
        int previousSize = i_map.size();
        
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        
        Atom doubleAtom = (Atom)i_map.put("double", new Atom("double"));
        Test.ensure(atom == doubleAtom);
        
        Test.ensure(i_map.size() == previousSize);
        i_map.put("double", doubleAtom);
        
        Test.commit();
        
        i_map.put("rollBack", "rollBack");
        i_map.put("double", new Atom("nono"));
        
        Test.rollBack();
        Test.ensure(i_map.get("rollBack") == null);
        Test.ensure(i_map.size() == previousSize);
        atom = (Atom)i_map.get("double");
        Test.ensure(atom == doubleAtom);
        Test.ensure(i_map.containsKey("double"));
        Test.ensure(! i_map.containsKey("rollBack"));
        
        otherMap.clear();
        otherMap.put("other1", doubleAtom);
        otherMap.put("other2", doubleAtom);
        
        i_map.putAll(otherMap);
        Test.objectContainer().deactivate(i_map, Integer.MAX_VALUE);
        
        Test.ensure(i_map.get("other1") == doubleAtom);
        Test.ensure(i_map.get("other2") == doubleAtom);
        
        
        i_map.clear();
        Test.ensure(i_map.size() == 0);
        setDefaultValues(i_map);
        
        int j = 0;
        i = i_map.keySet().iterator();
        while(i.hasNext()){
            String key = (String)i.next();
            if(key.equals("cool")){
                i.remove();
            }
            j++;
        }
        Test.ensure(i_map.size() == 2);
        Test.ensure(! i_map.containsKey("cool"));
        Test.ensure(j == 3);
        
        
        i_map.put("double", doubleAtom);
        ((Db4oMap)i_map).deleteRemoved(true);
        i_map.keySet().remove("double");
        Test.ensure(! Test.objectContainer().isStored(doubleAtom));
        ((Db4oMap)i_map).deleteRemoved(false);
        
        i_map.clear();
        Test.ensure(i_map.size() == 0);
        setDefaultValues(i_map);
    }
    
    private void tDefaultValues(){
        for (int i = 0; i < DEFAULT.length; i++) {
            Atom atom = (Atom)i_map.get(DEFAULT[i]);
            Test.ensure(atom.name.equals(DEFAULT[i]));
        }
    }
    
    private void tDefaultArray(Object[] arr){
        Test.ensure(arr.length == DEFAULT.length);
        String str[] = new String[DEFAULT.length];
        System.arraycopy(DEFAULT,0, str, 0, DEFAULT.length);
        for (int i = 0; i < arr.length; i++) {
            boolean found = false;
            for (int j = 0; j < str.length; j++) {
                if(arr[i].equals(str[j])){
                    str[j] = null;
                    found = true;
                }
            }
            Test.ensure(found);
        }
        for (int j = 0; j < str.length; j++) {
            Test.ensure(str[j] == null);
        }
    }

    
    private void restoreMembers(){
        Query q = Test.query();
        q.constrain(this.getClass());
        ObjectSet objectSet = q.execute();
        Db4oHashMap dll = (Db4oHashMap)objectSet.next();
        i_map = dll.i_map;
        i_helper = dll.i_helper;
    }
    
    private void lookupLast(){
        long start = System.currentTimeMillis();
        Atom atom = (Atom)i_map.get(MORE + (COUNT - 1));
        long stop = System.currentTimeMillis();
        // System.out.println("Time to look up element " + COUNT + ": " + (stop - start) + "ms");
        Test.ensure(atom.name. equals(MORE + (COUNT - 1)));
    }
    
    void checkHelper(Db4oHashMapHelper helper){
        ExtObjectContainer con = Test.objectContainer();
        if(con.isActive(helper)){
            Test.ensure(helper.i_childList.get(0).equals("hi"));
            checkHelper(helper.i_child);
        }
    }
    
    
    

}
