/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools.defragment;

import java.io.*;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.foundation.io.*;
import com.db4o.tools.defragment.*;

import db4ounit.extensions.fixtures.*;

public class Db4oDefragSolo extends Db4oSolo {
		public Db4oDefragSolo(ConfigurationSource configSource) {
			super(configSource);
		}

		protected ObjectContainer createDatabase(Configuration config) {
			File file=new File(getAbsolutePath());
			if(file.exists()) {
				try {
					String defragFile = getAbsolutePath()+".defrag";
					String mappingFile = getAbsolutePath()+".mapping";
					new File(defragFile).delete();
					new File(mappingFile).delete();
					SlotDefragment.defrag(getAbsolutePath(), defragFile, mappingFile, new DefragmentListener() {
						public void notifyDefragmentInfo(DefragmentInfo info) {
							System.err.println(info);
						}
					});
					File4.copy(defragFile,getAbsolutePath());
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