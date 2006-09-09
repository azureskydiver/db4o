/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;

public class Semaphores extends AllTestsConfAll{
	
	public void test(){
		
		ExtObjectContainer eoc = Test.objectContainer();
		eoc.setSemaphore("SEM", 0);
		
		Test.ensure(eoc.setSemaphore("SEM", 0) == true);
		
		if(Test.clientServer){
			ExtObjectContainer client2 = null;
			try {
				client2 =
					Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
				Test.ensure(client2.setSemaphore("SEM", 0) == false);
				eoc.releaseSemaphore("SEM");
				Test.ensure(client2.setSemaphore("SEM", 0) == true);
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}
		}else{
			eoc.releaseSemaphore("SEM");
		}
	}

}
