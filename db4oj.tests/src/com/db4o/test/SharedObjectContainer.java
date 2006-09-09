/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class SharedObjectContainer {
	
	String name;
	
	public void storeOne(){
		name = "hi";
	}
	
	public void testOne(){
		if(! Test.isClientServer() && ! Test.MEMORY_FILE){
			for (int i = 0; i < 30; i++) {
				ObjectContainer con = Db4o.openFile(Test.FILE_SOLO);
				Object obj = con.get(new SharedObjectContainer()).next();
				Test.ensure(obj == this);
				con.close();
            }
			Test.ensure(! Test.objectContainer().ext().isClosed());
		}
		
	}
	
	

}
