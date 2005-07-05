/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * The index record that is written to the database file.
 * Don't obfuscate.
 * 
 * @exclude
 * @persistent
 */
public class MetaIndex implements Internal{
    
    // The number of entries an the length are redundant, because the handler should
    // return a fixed length, but we absolutely want to make sure, we don't free
    // a slot into nowhere.
 
    public int indexAddress;
    public int indexEntries;
    public int indexLength;
    
	public int patchAddress;
	public int patchEntries;
	public int patchLength;
}
