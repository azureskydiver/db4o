/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;

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
					DefragmentConfig defragConfig = new DefragmentConfig(getAbsolutePath(), backupFile);
					defragConfig.forceBackupDelete(true);
					Defragment.defrag(defragConfig, new DefragmentListener() {
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