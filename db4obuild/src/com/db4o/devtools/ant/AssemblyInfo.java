package com.db4o.devtools.ant;

public enum AssemblyInfo {
    DB4O ("db4o", "db4o - database for objects"),
    DRS  ("dRS", "dRS - db4o Replication System");
    
		public static final String COPYRIGHT = "db4o 2005";
		public static final String COMPANY = "db4objects Inc., San Mateo, CA, USA";
    
    private final String title; 
    private final String product; 
    
    AssemblyInfo(String title, String product) {
    		this.title = title;
        this.product = product;
    }
    
    public String title()   { return title; }
    public String product()   { return product; }
}