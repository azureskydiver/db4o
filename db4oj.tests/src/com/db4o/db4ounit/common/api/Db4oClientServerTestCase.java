package com.db4o.db4ounit.common.api;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.mocking.*;

public class Db4oClientServerTestCase extends TestWithTempFile {
	
	private final class ClientServerFactoryStub extends MethodCallRecorder implements ClientServerFactory {
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

	public void testClientServerApi() {
		final ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		
		final ObjectServer server = Db4oClientServer.openServer(config, _tempFile, 0xdb40);
		try {
			server.grantAccess("user", "password");
			
			final ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			final ObjectContainer client1 = Db4oClientServer.openClient(clientConfig, "localhost", 0xdb40, "user", "password");
			try {
				
			} finally {
				Assert.isTrue(client1.close());
			}
		} finally {
			Assert.isTrue(server.close());
		}
	}
	
	public void testOpenServer() {
		final ClientServerFactoryStub factoryStub = new ClientServerFactoryStub();
		
		final ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		config.networking().factory(factoryStub);
		
		Assert.isNull(Db4oClientServer.openServer(config, _tempFile, 0xdb40));
		
		factoryStub.verify(new MethodCall[] {
			new MethodCall("openServer", new Object[] { MethodCall.IGNORED_ARGUMENT, _tempFile, 0xdb40, MethodCall.IGNORED_ARGUMENT }),
		});
	}
	
	public void testOpenClient() {

		final ClientServerFactoryStub factoryStub = new ClientServerFactoryStub();
		
		final ClientConfiguration config = Db4oClientServer.newClientConfiguration();
		config.networking().factory(factoryStub);
		
		Assert.isNull(Db4oClientServer.openClient(config, "foo", 42, "u", "p"));
		
		factoryStub.verify(new MethodCall[] {
			new MethodCall("openClient", new Object[] { MethodCall.IGNORED_ARGUMENT, "foo", 42, "u", "p", MethodCall.IGNORED_ARGUMENT }),
		});
	}
	
	public void testConfigurationHierarchy() {
		Assert.isInstanceOf(NetworkingConfigurationProvider.class, Db4oClientServer.newClientConfiguration());
		Assert.isInstanceOf(NetworkingConfigurationProvider.class, Db4oClientServer.newServerConfiguration());
	}
	
	
}
