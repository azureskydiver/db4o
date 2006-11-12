/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.tools.defragment.*;

import db4ounit.extensions.fixtures.*;

public class Db4oDefragSolo extends Db4oSolo {
		public Db4oDefragSolo(ConfigurationSource configSource) {
			super(configSource);
		}

		protected ObjectContainer createDatabase(Configuration config) {
			File origFile=new File(getAbsolutePath());
			if(origFile.exists()) {
				try {
					String backupFile = getAbsolutePath()+".defrag.backup";
					String mappingFile = getAbsolutePath()+".defrag.mapping";
					new File(backupFile).delete();
					new File(mappingFile).delete();
					SlotDefragment.defrag(new DefragmentConfig(getAbsolutePath(), backupFile, mappingFile), new DefragmentListener() {
						public void notifyDefragmentInfo(DefragmentInfo info) {
							System.err.println(info);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return super.createDatabase(config);
		}
		
		public boolean accept(Class clazz) {
			return !OptOutDefragSolo.class.isAssignableFrom(clazz);
		}
		
//		public void clean() {
//		}
	}