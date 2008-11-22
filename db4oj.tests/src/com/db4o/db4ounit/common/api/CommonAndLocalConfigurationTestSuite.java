package com.db4o.db4ounit.common.api;

import java.io.*;

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

public class CommonAndLocalConfigurationTestSuite extends FixtureBasedTestSuite {
	
	public static class BaseConfigurationProviderTestUnit implements TestCase {
		public static final class Item {
			
		}
		
		public void test() {
			final CommonConfigurationProvider config = subject();
			final Config4Impl legacy = legacyFrom(config);
			
			final CommonConfiguration common = config.common();
			common.activationDepth(42);		
			Assert.areEqual(42, legacy.activationDepth());
			Assert.areEqual(42, common.activationDepth());

			// TODO: assert
			common.add(new ConfigurationItem() {
				public void apply(InternalObjectContainer container) {
				}

				public void prepare(Configuration configuration) {
				}
			});
			
			final TypeAlias alias = new TypeAlias("foo", "bar");
			common.addAlias(alias);
			Assert.areEqual("bar", legacy.resolveAliasStoredName("foo"));
			Assert.areEqual("foo", legacy.resolveAliasRuntimeName("bar"));
			
			common.removeAlias(alias);
			Assert.areEqual("foo", legacy.resolveAliasStoredName("foo"));

			
			common.allowVersionUpdates(false);
			Assert.isFalse(legacy.allowVersionUpdates());
			
			common.automaticShutDown(false);
			Assert.isFalse(legacy.automaticShutDown());
			
			common.bTreeNodeSize(42);
			Assert.areEqual(42, legacy.bTreeNodeSize());
			
			common.callbacks(false);
			Assert.isFalse(legacy.callbacks());
			
			common.callConstructors(false);
			Assert.isTrue(legacy.callConstructors().definiteNo());
			
			common.detectSchemaChanges(false);
			Assert.isFalse(legacy.detectSchemaChanges());
			
			final DiagnosticCollector collector = new DiagnosticCollector();
			common.diagnostic().addListener(collector);
			final Diagnostic diagnostic = dummyDiagnostic();
			legacy.diagnosticProcessor().onDiagnostic(diagnostic);
			collector.verify(diagnostic);

			common.exceptionsOnNotStorable(true);
			Assert.isTrue(legacy.exceptionsOnNotStorable());
			
			common.internStrings(true);
			Assert.isTrue(legacy.internStrings());

			// TODO: assert
			common.markTransient("Foo");
			
			common.messageLevel(3);
			Assert.areEqual(3, legacy.messageLevel());
			
			ObjectClass objectClass = common.objectClass(Item.class);
			objectClass.cascadeOnDelete(true);
			Assert.isTrue(((Config4Class)legacy.objectClass(Item.class)).cascadeOnDelete().definiteYes());
			Assert.isTrue(((Config4Class)common.objectClass(Item.class)).cascadeOnDelete().definiteYes());
			
			common.optimizeNativeQueries(false);
			Assert.isFalse(legacy.optimizeNativeQueries());
			Assert.isFalse(common.optimizeNativeQueries());
			
			common.queries().evaluationMode(QueryEvaluationMode.LAZY);
			Assert.areEqual(QueryEvaluationMode.LAZY, legacy.queryEvaluationMode());
			
			// TODO: test reflectWith()
			
			// TODO: test refreshClasses()
			
			// TODO: this probably won't sharpen :/
			PrintStream outStream = System.out;
			common.outStream(outStream);
			Assert.areEqual(outStream, legacy.outStream());

			StringEncoding stringEncoding = new StringEncoding() {
				public String decode(byte[] bytes, int start, int length) {
					return null;
				}

				public byte[] encode(String str) {
					return null;
				}			
			};
			common.stringEncoding(stringEncoding);
			Assert.areEqual(stringEncoding, legacy.stringEncoding());
			
			common.testConstructors(false);
			Assert.isFalse(legacy.testConstructors());
			common.testConstructors(true);
			Assert.isTrue(legacy.testConstructors());
			
			common.updateDepth(1024);
			Assert.areEqual(1024, legacy.updateDepth());
			
			common.weakReferences(false);
			Assert.isFalse(legacy.weakReferences());
			
			common.weakReferenceCollectionInterval(1024);
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
			if (CommonAndLocalConfigurationTestSuite.<Object>subject() instanceof ClientConfiguration) {
				return;
			}
			
			final FileConfigurationProvider config = subject();
			final FileConfiguration fileConfig = config.file();
			final Config4Impl legacyConfig = legacyFrom(config);
			
			fileConfig.blockSize(42);
			Assert.areEqual(42, legacyConfig.blockSize());
			
			fileConfig.databaseGrowthSize(42);
			Assert.areEqual(42, legacyConfig.databaseGrowthSize());
			
			fileConfig.disableCommitRecovery();
			Assert.isTrue(legacyConfig.commitRecoveryDisabled());
			
			fileConfig.freespace().discardSmallerThan(8);
			Assert.areEqual(8, legacyConfig.discardFreeSpace());
			
			fileConfig.generateUUIDs(ConfigScope.GLOBALLY);
			Assert.areEqual(ConfigScope.GLOBALLY, legacyConfig.generateUUIDs());

			fileConfig.generateVersionNumbers(ConfigScope.GLOBALLY);
			Assert.areEqual(ConfigScope.GLOBALLY, legacyConfig.generateVersionNumbers());
			
			Storage storageFactory = new FileStorage();
			fileConfig.storageFactory(storageFactory);
			Assert.areSame(storageFactory, legacyConfig.storage());
			Assert.areSame(storageFactory, fileConfig.storageFactory());
			
			fileConfig.lockDatabaseFile(true);
			Assert.isTrue(legacyConfig.lockFile());
			
			fileConfig.reserveStorageSpace(1024);
			Assert.areEqual(1024, legacyConfig.reservedStorageSpace());
			
			fileConfig.blobPath(Path4.getTempPath());
			Assert.areEqual(Path4.getTempPath(), legacyConfig.blobPath());
			
			fileConfig.readOnly(true);
			Assert.isTrue(legacyConfig.isReadOnly());
			
			CacheConfigurationProvider cacheProvider = subject();
			final CacheConfiguration cache = cacheProvider.cache();
			
			cache.slotCacheSize(30);
			Assert.areEqual(30, legacyConfig.slotCacheSize());
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
