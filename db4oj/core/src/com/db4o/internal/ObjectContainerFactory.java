/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;


public class ObjectContainerFactory {
	
	public static ObjectContainer openObjectContainer(Configuration config,String databaseFileName) {
		
		ObjectContainer oc = null;
		
		if (Deploy.debug) {
			System.out.println("db4o Debug is ON");
			if (!Deploy.flush) {
				System.out.println("Debug option set NOT to flush file.");
			}
		}
		try {
			oc = new IoAdaptedObjectContainer(config,databaseFileName);				
		} catch (DatabaseFileLockedException e) {
			throw e;
		} catch (ObjectNotStorableException e) {
			throw e;
		} catch (Db4oException e) {
			throw e;
		} catch (Exception ex) {
			Messages.logErr(Db4o.configure(), 4, databaseFileName, ex);
			if(Deploy.debug){
				ex.printStackTrace();
			}
			return null;
		}
			
		Platform4.postOpen(oc);
		Messages.logMsg(Db4o.configure(), 5, databaseFileName);
		return oc;
	}
	
	

	
}
