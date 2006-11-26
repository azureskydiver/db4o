/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Class metadata to be stored to the database file
 * Don't obfuscate.
 * 
 * @exclude
 * @persistent
 */
public class MetaClass implements Internal4{
	
	/** persistent field, don't touch */
	public String name;
    
    /** persistent field, don't touch */
	public MetaField[] fields;

}
