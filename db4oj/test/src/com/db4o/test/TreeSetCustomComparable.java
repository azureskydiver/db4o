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
        Map map=new TreeMap();
        map.put(new TreeSetCustomComparable(),new TreeSet());
        Test.objectContainer().set(map);
    }
    
    public void test(){
        TreeMap map=new TreeMap();
        ObjectSet result=Test.objectContainer().get(map);
        while(result.hasNext()) {
          System.err.println(result.next());
        }
    }
}
