/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.activating;


public class SensorPanel {

	public Object sensor;
	public SensorPanel next;
	
	public SensorPanel(){
		// default constructor for instantiation
	}
	
	public SensorPanel(int value){
		sensor = new Integer(value);
	}
	
	public SensorPanel createList(int length){
		return createList(length, 1);
	}
	
	public SensorPanel createList(int length, int first){
		int val = first;
		SensorPanel root = newElement(first);
		SensorPanel list = root;
		while(--length > 0){
			list.next = newElement(++ val);
			list = list.next;
		}
		return root;
	}
	
	protected SensorPanel newElement(int value){
		return new SensorPanel(value);
	}
		
	  public String toString() {
	        return "Sensor #" + sensor ;
	    }
}
