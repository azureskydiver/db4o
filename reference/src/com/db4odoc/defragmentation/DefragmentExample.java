/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.defragmentation;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.defragment.AvailableClassFilter;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.defragment.DefragmentInfo;
import com.db4o.defragment.DefragmentListener;
import com.db4o.defragment.TreeIDMapping;


public class DefragmentExample {

	private static final String DB_FILE = "test.db4o";
	private static final String BACKUP_FILE = "test.bap";
	
	public static void main(String[] args){
		simplestDefragment();
		configuredDefragment();
		defragmentWithListener();
	}
	// end main
	
	private static void simplestDefragment(){
		try {
	    	Defragment.defrag(DB_FILE);
	    } catch (IOException ex){
	    	System.out.println(ex.toString());
	    }
	}
	// end simplestDefragment
	
	private static void configuredDefragment(){
		DefragmentConfig config=new DefragmentConfig(DB_FILE, BACKUP_FILE, new TreeIDMapping());
		config.objectCommitFrequency(5000);
		config.db4oConfig(Db4o.cloneConfiguration());
		config.forceBackupDelete(true);
		config.storedClassFilter(new AvailableClassFilter());
		config.upgradeFile(DB_FILE + ".upg");
	    try {
	    	Defragment.defrag(config);
	    } catch (Exception ex){
	    	System.out.println(ex.toString());
	    }
	}
	// end configuredDefragment
	
	private static void defragmentWithListener(){
		DefragmentConfig config=new DefragmentConfig(DB_FILE);
		try {
	    	Defragment.defrag(config, new DefragmentListener() {
				public void notifyDefragmentInfo(DefragmentInfo info) {
					System.err.println(info);
				}
			});
	    } catch (Exception ex){
	    	System.out.println(ex.toString());
	    }
	}
	// end defragmentWithListener
}
