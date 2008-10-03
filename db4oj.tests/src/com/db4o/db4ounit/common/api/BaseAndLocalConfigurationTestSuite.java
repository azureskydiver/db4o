package com.db4o.db4ounit.common.api;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.encoding.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class BaseAndLocalConfigurationTestSuite extends FixtureBasedTestSuite {
	
	public static class BaseConfigurationProviderTestUnit implements TestCase {
		public void test() {
			final BaseConfigurationProvider config = subject();
			final Config4Impl legacy = legacyFrom(config);
			
			final BaseConfiguration base = config.base();
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
	}
	
	public static class LocalConfigurationProviderTestUnit implements TestCase {
		public void test() throws Exception {
			if (subject() instanceof ClientConfiguration) {
				return;
			}
			
			final LocalConfigurationProvider config = subject();
			final LocalConfiguration local = config.local();
			final Config4Impl legacy = legacyFrom(config);
			
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
		
	}
	
	@Override
    public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] { subjects(Db4oEmbedded.newConfiguration(), Db4oClientServer.newClientConfiguration(), Db4oClientServer.newServerConfiguration()) };
    }

	private FixtureProvider subjects(Object... subjects) {
		return new SubjectFixtureProvider(subjects);
    }

	@Override
    public Class[] testUnits() {
		return new Class[] {
			BaseConfigurationProviderTestUnit.class,
			LocalConfigurationProviderTestUnit.class,
		};
    }
	
	private static Config4Impl legacyFrom(final Object config) {
		return ((LegacyConfigurationProvider)config).legacy();
	}
	
	public static <T> T subject() {
		return (T) SubjectFixtureProvider.value();
	}

}
