/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;

/**
 * 
 */
public class TreeSetCustomComparable implements Comparable{
    
    public Set path;

    public TreeSetCustomComparable() {
      this.path = new TreeSet();
    }

    public int compareTo(Object that) {
      return hashCode()-that.hashCode();
    }
    
    public void store(){
        Test.deleteAllInstances(TreeMap.class);
        Map map=new TreeMap();
        map.put(new TreeSetCustomComparable(),new TreeSet());
        Test.objectContainer().set(map);
    }
    
    public void test(){
        TreeMap map=new TreeMap();
        ObjectSet result=Test.objectContainer().get(map);
        while(result.hasNext()) {
            TreeMap tm = (TreeMap)result.next();
            Test.ensure(tm.size() == 1);
            Iterator i = tm.keySet().iterator();
            Test.ensure(i.hasNext());
            TreeSetCustomComparable tscc = (TreeSetCustomComparable)i.next();
            TreeSet ts = (TreeSet)tm.get(tscc);
            Test.ensure(ts != null);
        }
    }
}
