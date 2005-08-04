/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

public class SwitchingFilesFromClient {
	
	static final String DB_FILE = "switchedToTest.yap";
	
	public String name;
	
	void storeOne(){
		name = "helo";
		new File(DB_FILE).delete();
	}
	
	void testOne(){
		
		if(Test.isClientServer()){
			Test.ensure(name.equals("helo"));
			
			ExtClient client = (ExtClient)Test.objectContainer();
			client.switchToFile(DB_FILE);
			name = "hohoho";
			client.set(this);
			Query q = client.query();
			q.constrain(this.getClass());
			ObjectSet results = q.execute();
			Test.ensure(results.size() == 1);
			SwitchingFilesFromClient sffc = (SwitchingFilesFromClient) results.next();
			Test.ensure(sffc.name.equals("hohoho"));
			client.switchToMainFile();
			sffc = (SwitchingFilesFromClient)Test.getOne(this);
			Test.ensure(sffc.name.equals("helo"));
		}
		
		
	}

}
