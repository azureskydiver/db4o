/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.lists;

import java.util.List;


public class Team {
	private List pilots;
	private String name;
	
	public Team(){
		pilots = CollectionFactory.newList();
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void addPilot(Pilot pilot){
		pilots.add(pilot);
	}
	
	public Pilot getPilot(int index){
		return (Pilot)pilots.get(index); 
	}
	
	public void removePilot(int index){
		pilots.remove(index);
	}
	
	public void updatePilot(int index, Pilot newPilot){
		pilots.set(index, newPilot);
	}
}
