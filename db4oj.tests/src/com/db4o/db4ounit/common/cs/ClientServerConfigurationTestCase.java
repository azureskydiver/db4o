/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.mocking.*;

/**
 * @sharpen.ignore
 */
public class ClientServerConfigurationTestCase extends AbstractDb4oTestCase{
	
	protected void configure(Configuration config) throws Exception {
		// Just make sure no exception is thrown when
		// Class.forName() runs in DotNetSupport. 
		config.add(new DotnetSupport(true));
	}
	
	public void testDotNetSupport(){
		// For now: Just make sure a database file is opened.
		Assert.isTrue(true);
	}
	
	static final class ClientServerFactoryStub extends MethodCallRecorder implements ClientServerFactory {
		public ObjectContainer openClient(Configuration config,
				String hostName, int port, String user, String password,
				NativeSocketFactory socketFactory) throws Db4oIOException,
				OldFormatException, InvalidPasswordException {
			
			record(new MethodCall("openClient", new Object[] { config, hostName, port, user, password, socketFactory }));
			return null;
		}

		public ObjectServer openServer(Configuration config,
				String databaseFileName, int port,
				NativeSocketFactory socketFactory) throws Db4oIOException,
				IncompatibleFileFormatException, OldFormatException,
				DatabaseFileLockedException, DatabaseReadOnlyException {
			
			record(new MethodCall("openServer", new Object[] { config, databaseFileName, port, socketFactory }));
			return null;
		}
	}
	

	public void testOpenServer() {
		final ClientServerFactoryStub factoryStub = new ClientServerFactoryStub();
		
		final Configuration config = stubbedConfigurationFor(factoryStub);
		
		Assert.isNull(Db4o.openServer(config, "file.db4o", 0xdb40));
		
		factoryStub.verify(new MethodCall[] {
			new MethodCall("openServer", new Object[] { MethodCall.IGNORED_ARGUMENT, "file.db4o", 0xdb40, MethodCall.IGNORED_ARGUMENT }),
		});
	}

	
	public void testOpenClient() {

		final ClientServerFactoryStub factoryStub = new ClientServerFactoryStub();
		
		final Configuration config = stubbedConfigurationFor(factoryStub);
		
		Assert.isNull(Db4o.openClient(config, "foo", 42, "u", "p"));
		
		factoryStub.verify(new MethodCall[] {
			new MethodCall("openClient", new Object[] { MethodCall.IGNORED_ARGUMENT, "foo", 42, "u", "p", MethodCall.IGNORED_ARGUMENT }),
		});
	}
	
	private Configuration stubbedConfigurationFor(final ClientServerFactoryStub factoryStub) {
		final Configuration config = Db4o.newConfiguration();
		config.clientServer().factory(factoryStub);
		return config;
	}

}
