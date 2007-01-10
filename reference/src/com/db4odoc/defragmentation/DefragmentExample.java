/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.defragmentation;


public class DefragmentExample {

	public static void main(String[] args){
		runDefragment();
	}
	// end main
	
	public static void runDefragment(){
		com.db4o.defragment.DefragmentConfig config=
			new com.db4o.defragment.DefragmentConfig("sample.yap","sample.bap");
		config.forceBackupDelete(true);
		config.storedClassFilter(new com.db4o.defragment.AvailableClassFilter());
	    try {
	    	com.db4o.defragment.Defragment.defrag(config);
	    } catch (Exception ex){
	    	System.out.println(ex.toString());
	    }
	}
	// end runDefragment
}
