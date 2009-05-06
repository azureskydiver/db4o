/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Field MetaData to be stored to the database file.
 * Don't obfuscate.
 * 
 * @exclude
 * @persistent
 */
public class MetaField implements Internal4{
	
	public String name;
	
	public MetaIndex index;
	
}
