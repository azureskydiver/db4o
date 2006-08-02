/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * The index record that is written to the database file.
 * Don't obfuscate.
 * 
 * @exclude
 * @persistent
 */
public class MetaIndex implements Internal4{
    
    // The number of entries an the length are redundant, because the handler should
    // return a fixed length, but we absolutely want to make sure, we don't free
    // a slot into nowhere.
 
    public int indexAddress;
    public int indexEntries;
    public int indexLength;
    
    // TODO: make sure this aren't really needed
    // and remove them 
	private final int patchAddress = 0;
	private final int patchEntries = 0;
	private final int patchLength = 0;
    
    public void read(YapReader reader){
        indexAddress = reader.readInt();
        indexEntries = reader.readInt();
        indexLength = reader.readInt();
        
        // no longer used apparently
        /*patchAddress = */reader.readInt();
        /*patchEntries = */reader.readInt();
        /*patchLength = */reader.readInt();
    }
    
    public void write(YapWriter writer){
        writer.writeInt(indexAddress);
        writer.writeInt(indexEntries);
        writer.writeInt(indexLength);
        writer.writeInt(patchAddress);
        writer.writeInt(patchEntries);
        writer.writeInt(patchLength);
    }
    
    public void free(YapFile file){
        file.free(indexAddress, indexLength);        
        indexAddress = 0;
        indexLength = 0;
//        file.free(patchAddress, patchLength);
//        patchAddress = 0;
//        patchLength = 0;
    }
}
