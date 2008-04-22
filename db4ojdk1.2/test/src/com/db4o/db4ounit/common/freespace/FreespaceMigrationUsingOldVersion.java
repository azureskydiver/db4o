/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.*;
import com.db4o.db4ounit.common.defragment.PathProvider;
import com.db4o.db4ounit.common.migration.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class FreespaceMigrationUsingOldVersion implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(FreespaceMigrationUsingOldVersion.class).run();
	}
	
	public void _test() throws Exception {
		final String fileName = Path4.getTempFileName();
		File4.delete(fileName);
		usingVersion("5.5", fileName, "create");
		upgrade(fileName);
		update(fileName, false);
		update(fileName, false);
	}

	public void update (String fname, boolean useRamSystem) throws Exception {
		if(useRamSystem){
			System.out.println("useRamSystem");
			Db4o.configure().freespace().useRamSystem();
		}else{
			System.out.println("useBTreeSystem");
			Db4o.configure().freespace().useBTreeSystem();
		}
		Db4o.configure().allowVersionUpdates(false);
		ObjectContainer oc = Db4o.openFile(fname);
		System.out.println(oc.ext().systemInfo().freespaceSize());
		System.out.println(oc.ext().systemInfo().freespaceEntryCount());
		oc.close();		
	}

	public void upgrade (String fname) throws Exception {
		Db4o.configure().allowVersionUpdates(true);
		ObjectContainer oc = Db4o.openFile(fname);
		System.out.println(oc.ext().systemInfo().freespaceSize());
		System.out.println(oc.ext().systemInfo().freespaceEntryCount());
		oc.close();
	}

	public void create(String fname) throws Exception {
		ObjectContainer oc = Db4o.openFile(fname);
		oc.commit();
		oc.close();
	}
	
	private void usingVersion(String version, String dbFile, String methodName) throws Exception{
		Db4oLibrary library = librarian().forVersion(version);
		library.environment.invokeInstanceMethod(getClass(), methodName, new Object[] { dbFile });
	}
	
	private Db4oLibrarian librarian() {
		return new Db4oLibrarian(new Db4oLibraryEnvironmentProvider(PathProvider.testCasePath()));
	}


}
