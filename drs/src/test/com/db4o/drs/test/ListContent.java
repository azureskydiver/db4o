/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.test;


public class ListContent {
    
    private String name;
    
    public ListContent(){
        
    }
    
    public ListContent(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

	public String toString() {
		return "name = " + name;
	}
}
