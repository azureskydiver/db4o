/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.collections;

import java.util.*;


public class ListHolder {
    
    private String name;
    
    private List list;
    
    public ListHolder(){
        
    }
    
    public ListHolder(String name){
        this.name = name;
    }
    
    public void add(ListContent obj){
        list.add(obj);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List getList() {
        return list;
    }
    
    public void setList(List list) {
        this.list = list;
    }

	public String toString() {
		return "name = " + name + ", list = " + list;
	}
}
