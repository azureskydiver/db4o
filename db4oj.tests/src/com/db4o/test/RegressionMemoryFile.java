/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * Runs the Regression test on a memory file
 */
public class RegressionMemoryFile extends Regression {
	
	ObjectContainer con;

	public static void main(String[] args) {
		System.out.println("Memory File Regression Test");
		Db4o.configure().messageLevel(-1);
		new RegressionMemoryFile().run();
	}
	
	public RegressionMemoryFile(){
		configure();
		MemoryFile mf = new MemoryFile();
		con= ExtDb4o.openMemoryFile(mf);
	}
	
	public void completed(){
		con.close();
	}

	public ObjectContainer openContainer() {
		return con;
	}
	
	public void close(ObjectContainer container) {
		// don't close
	}
	
	protected void closeAllButMemoryFile(ObjectContainer container){
		
	}
	
	
}
