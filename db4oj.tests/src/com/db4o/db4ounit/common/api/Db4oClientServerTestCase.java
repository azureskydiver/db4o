package com.db4o.db4ounit.common.api;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.encoding.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.config.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.mocking.*;

public class Db4oClientServerTestCase extends TestWithTempFile {
	
	private static final class DiagnosticCollector implements DiagnosticListener {
		
		ArrayList<Diagnostic> _diagnostics = new ArrayList<Diagnostic>();
		
		public void onDiagnostic(Diagnostic d) {
			_diagnostics.add(d);
		}

		public void verify(Object... expected) {
			ArrayAssert.areEqual(expected, _diagnostics.toArray());
		}
	}

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
	
	public void testServerConfigurationLocal() throws Exception {
		
		final ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		
		final Config4Impl legacy = legacyFrom(config);
		final LocalConfiguration local = config.local();
		final TypeAlias alias = new TypeAlias("foo", "bar");
		local.addAlias(alias);
		Assert.areEqual("bar", legacy.resolveAliasStoredName("foo"));
		Assert.areEqual("foo", legacy.resolveAliasRuntimeName("bar"));
		
		local.removeAlias(alias);
		Assert.areEqual("foo", legacy.resolveAliasStoredName("foo"));
		
		local.blockSize(42);
		Assert.areEqual(42, legacy.blockSize());
		
		local.databaseGrowthSize(42);
		Assert.areEqual(42, legacy.databaseGrowthSize());
		
		local.disableCommitRecovery();
		Assert.isTrue(legacy.commitRecoveryDisabled());
		
		local.freespace().discardSmallerThan(8);
		Assert.areEqual(8, legacy.discardFreeSpace());
		
		local.generateUUIDs(ConfigScope.GLOBALLY);
		Assert.areEqual(ConfigScope.GLOBALLY, legacy.generateUUIDs());

		local.generateVersionNumbers(ConfigScope.GLOBALLY);
		Assert.areEqual(ConfigScope.GLOBALLY, legacy.generateVersionNumbers());
		
		MemoryIoAdapter ioAdapter = new MemoryIoAdapter();
		local.io(ioAdapter);
		Assert.areEqual(ioAdapter, legacy.io());
		Assert.areEqual(ioAdapter, local.io());
		
		local.lockDatabaseFile(true);
		Assert.isTrue(legacy.lockFile());
		
		local.reserveStorageSpace(1024);
		Assert.areEqual(1024, legacy.reservedStorageSpace());
		
		local.blobPath(Path4.getTempPath());
		Assert.areEqual(Path4.getTempPath(), legacy.blobPath());
	}
	
	public void testServerConfigurationBase() {
		final ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		Config4Impl legacy = legacyFrom(config);
		BaseConfiguration base = config.base();
		base.activationDepth(42);		
		Assert.areEqual(42, legacy.activationDepth());
		Assert.areEqual(42, base.activationDepth());

		// TODO: assert
		base.add(new ConfigurationItem() {
			public void apply(InternalObjectContainer container) {
			}

			public void prepare(Configuration configuration) {
			}
		});
		
		base.allowVersionUpdates(false);
		Assert.isFalse(legacy.allowVersionUpdates());
		
		base.automaticShutDown(false);
		Assert.isFalse(legacy.automaticShutDown());
		
		base.bTreeNodeSize(42);
		Assert.areEqual(42, legacy.bTreeNodeSize());
		
		base.callbacks(false);
		Assert.isFalse(legacy.callbacks());
		
		base.callConstructors(false);
		Assert.isTrue(legacy.callConstructors().definiteNo());
		
		base.detectSchemaChanges(false);
		Assert.isFalse(legacy.detectSchemaChanges());
		
		final DiagnosticCollector collector = new DiagnosticCollector();
		base.diagnostic().addListener(collector);
		final Diagnostic diagnostic = dummyDiagnostic();
		legacy.diagnosticProcessor().onDiagnostic(diagnostic);
		collector.verify(diagnostic);

		base.exceptionsOnNotStorable(true);
		Assert.isTrue(legacy.exceptionsOnNotStorable());
		
		base.internStrings(true);
		Assert.isTrue(legacy.internStrings());

		// TODO: assert
		base.markTransient("Foo");
		
		base.messageLevel(3);
		Assert.areEqual(3, legacy.messageLevel());
		
		ObjectClass objectClass = base.objectClass(Collection.class);
		objectClass.cascadeOnDelete(true);
		Assert.isTrue(((Config4Class)legacy.objectClass(Collection.class)).cascadeOnDelete().definiteYes());
		Assert.isTrue(((Config4Class)base.objectClass(Collection.class)).cascadeOnDelete().definiteYes());
		
		base.optimizeNativeQueries(false);
		Assert.isFalse(legacy.optimizeNativeQueries());
		Assert.isFalse(base.optimizeNativeQueries());
		
		base.queries().evaluationMode(QueryEvaluationMode.LAZY);
		Assert.areEqual(QueryEvaluationMode.LAZY, legacy.queryEvaluationMode());
		
		base.readOnly(true);
		Assert.isTrue(legacy.isReadOnly());
		
		// TODO: test reflectWith()
		
		// TODO: test refreshClasses()
		
		// TODO: this probably won't sharpen :/
		PrintStream outStream = System.out;
		base.outStream(outStream);
		Assert.areEqual(outStream, legacy.outStream());

		StringEncoding stringEncoding = new StringEncoding() {
			public String decode(byte[] bytes, int start, int length) {
				return null;
			}

			public byte[] encode(String str) {
				return null;
			}			
		};
		base.stringEncoding(stringEncoding);
		Assert.areEqual(stringEncoding, legacy.stringEncoding());
		
		base.testConstructors(false);
		Assert.isFalse(legacy.testConstructors());
		base.testConstructors(true);
		Assert.isTrue(legacy.testConstructors());
		
		base.updateDepth(1024);
		Assert.areEqual(1024, legacy.updateDepth());
		
		base.weakReferences(false);
		Assert.isFalse(legacy.weakReferences());
		
		base.weakReferenceCollectionInterval(1024);
		Assert.areEqual(1024, legacy.weakReferenceCollectionInterval());
		
		// TODO: test registerTypeHandler()
	}

	private DiagnosticBase dummyDiagnostic() {
		return new DiagnosticBase() {
			@Override
			public String problem() {
				return null;
			}

			@Override
			public Object reason() {
				return null;
			}

			@Override
			public String solution() {
				return null;
			}
		};
	}

	private Config4Impl legacyFrom(final ServerConfiguration config) {
		return ((NetworkingConfigurationImpl)config.networking()).config();
	}
}
