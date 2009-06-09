/* Copyright (C) 2009   Versant Inc.   http://www.db4o.com */
/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.common.ext;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

public class UnavailableClassesWithTypeHandlerTestCase extends TestWithTempFile implements OptOutNetworkingCS {
	
	public static class HolderForClassWithTypeHandler {
		public HolderForClassWithTypeHandler(Stack stack) {
			_fieldWithTypeHandler = stack;
		}

		public Stack _fieldWithTypeHandler;
	}

	/**
	 * @sharpen.ignore
	 */
	@decaf.Ignore(decaf.Platform.JDK11)
	public void testDefrag() {
		final Throwable e = Assert.expect(IllegalStateException.class, new CodeBlock() { public void run() throws Throwable {
			final DefragmentConfig config = new DefragmentConfig(tempFile());
			config.db4oConfig(configWith(new com.db4o.reflect.jdk.JdkReflector(new ExcludingClassLoader(getClass().getClassLoader(), Stack.class))));
			Defragment.defrag(config);
		}});
		Assert.isTrue(e.getMessage().indexOf("_fieldWithTypeHandler") >= 0, "Message should contain the offending field.");
	}

	public void testStoredClasses() {		
		assertStoredClasses(tempFile());
	}
	
	@Override
	public void setUp() throws Exception {
	    super.setUp();
	    store(tempFile(), new HolderForClassWithTypeHandler(new Stack()));
	}

	private void assertStoredClasses(final String databaseFileName) {
		ObjectContainer db = openFileExcludingStackClass(databaseFileName);
		try {
			Assert.isGreater(2, db.ext().storedClasses().length);
		} finally {
			db.close();
		}
	}

	private EmbeddedObjectContainer openFileExcludingStackClass(final String databaseFileName) {
	    return Db4oEmbedded.openFile(configExcludingStack(), databaseFileName);
    }

	private void store(final String databaseFileName, Object obj) {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), databaseFileName);
		try {
			db.store(obj);
		} finally {
			db.close();
		}
	}

	private EmbeddedConfiguration configExcludingStack() {
		return configWith(new ExcludingReflector(Stack.class));
	}

	private EmbeddedConfiguration configWith(final Reflector reflector) {
	    final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();			
		config.common().reflectWith(reflector);
		return config;
    }
}
