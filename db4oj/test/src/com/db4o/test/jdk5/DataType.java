package com.db4o.test.jdk5;


public enum DataType {
    A("A"),
    B("B");
    
    private String type;
    private int count;

    private DataType(String type) {
       this.type = type;
       this.count=0;
    }

    public String getType() {
       return "type "+type;
    }
    
    public int getCount() {
    	return count;
    }
    
    public void incCount() {
    	count++;
    }
 }
