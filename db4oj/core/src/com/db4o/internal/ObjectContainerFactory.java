/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;


public class ObjectContainerFactory {
	
	public static ObjectContainer openObjectContainer(Configuration config,
			String databaseFileName) throws OldFormatException {		
		if (Deploy.debug) {
			System.out.println("db4o Debug is ON");
			if (!Deploy.flush) {
				System.out.println("Debug option set NOT to flush file.");
			}
		}		
		ObjectContainer oc = new IoAdaptedObjectContainer(config, databaseFileName);	
		Messages.logMsg(config, 5, databaseFileName);
		return oc;
	}
	
	

	
}
