/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.config.*;
import com.db4o.internal.cs.*;

import db4ounit.extensions.*;

public class SwitchingToFileWithDifferentClassesTestCase extends StandaloneCSTestCaseBase {

	public static class Data1 {
		public int _id;

		public Data1(int id) {
			this._id = id;
		}

		@Override
        public boolean equals(Object obj) {
	        if (this == obj)
		        return true;
	        if (obj == null)
		        return false;
	        if (getClass() != obj.getClass())
		        return false;
	        Data1 other = (Data1) obj;
	        if (_id != other._id)
		        return false;
	        return true;
        }
	}
	
	public static class Data2 extends Data1 {
		public Data2(int id) {
	        super(id);
        }
	}
	
	@Override
	protected void configure(Configuration config) {	
		config.encrypt(false);
	}

	@Override
	protected void runTest() throws Throwable {
		
		ClientObjectContainer clientA = openClient();
		clientA.store(new Data1(1));
		
		ClientObjectContainer clientB = openClient();
		clientB.store(new Data1(2));
		clientB.commit();
		
		clientB.switchToFile(SwitchingFilesFromClientUtil.FILENAME_B);
		
		final Data2 data2 = new Data2(3);
		clientA.store(data2);
		clientA.commit();
		
		clientB.switchToMainFile();
		
		ObjectSetAssert.sameContent(clientB.query(Data2.class), data2);
		
	}

}
