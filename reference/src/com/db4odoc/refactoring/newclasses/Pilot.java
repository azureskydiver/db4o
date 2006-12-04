/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.refactoring.newclasses;

public class Pilot {
private Identity name;
    
    public Pilot(String name, String id) {
        this.name = new Identity(name,id);
    }

    public Identity getName() {
        return name;
    }

    public void setName(Identity name){
    	this.name = name;
    }
    
    public String toString() {
    	if (name == null){
    		return "Unidentified pilot";
    	} else {
    		return name.toString();
    	}
    }
}
