/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.versionupdate;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

public class UpdateExample {

	public static void main(String[] args) {
		Db4o.configure().allowVersionUpdates(true);
		ObjectContainer objectContainer = Db4o.openFile(args[0]);
		objectContainer.close();
		System.out.println("The database is ready for the version " + Db4o.version());
	}

}
