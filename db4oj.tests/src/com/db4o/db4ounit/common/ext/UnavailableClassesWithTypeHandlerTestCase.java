/* Copyright (C) 2009   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ext;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class UnavailableClassesWithTypeHandlerTestCase extends TestWithTempFile implements OptOutNetworkingCS {
	
	public static class HolderForClassWithTypeHandler {
		public HolderForClassWithTypeHandler(Stack stack) {
			_fieldWithTypeHandler = stack;
		}

		public Stack _fieldWithTypeHandler;
	}
	
	public static void main(String[] args) {
		new ConsoleTestRunner(UnavailableClassesWithTypeHandlerTestCase.class).run();
	}

	public void testStoredClassesWithTypeHandler() {		
		store(tempFile(), new HolderForClassWithTypeHandler(new Stack()));
		assertStoredClasses(tempFile());
	}

	private void assertStoredClasses(final String databaseFileName) {
		ObjectContainer db = Db4oEmbedded.openFile(configExcludingStack(), databaseFileName);

		try {
			Assert.isGreater(2, db.ext().storedClasses().length);
		} finally {
			db.close();
		}
	}

	private void store(final String databaseFileName, Object obj) {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), databaseFileName);
		try {
			db.store(obj);
		}
		finally {
			db.close();
		}
	}

	private EmbeddedConfiguration configExcludingStack() {
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();				
		config.common().reflectWith(new ExcludingReflector(Stack.class));
		return config;
	}
}
