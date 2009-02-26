package com.db4o.db4ounit.common.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

/**
 * Tests all combinations of configuration use/reuse scenarios.
 * 
 * @deprecated tests deprecated api
 */
public class ConfigurationReuseTestSuite extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(
			new SimpleFixtureProvider(
				CONFIGURATION_USE_FUNCTION, // each function returns a block that disposes of any containers
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final ObjectContainer container = Db4o.openFile(config, ".");
					return new Runnable() { public void run() {
						container.close();
					}};
				}},
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final ObjectServer server = Db4o.openServer(config, ".", 0);
					return new Runnable() { public void run() {
						server.close();
					}};
				}},
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final Configuration serverConfig = Db4o.newConfiguration();
					serverConfig.storage(new MemoryStorage());
					final ObjectServer server = Db4o.openServer(serverConfig, ".", 0);
					final ObjectContainer client = server.openClient(config);
					return new Runnable() { public void run() {
						client.close();
						server.close();
					}};
				}}
			),
			new SimpleFixtureProvider(
				CONFIGURATION_REUSE_PROCEDURE,
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					Db4o.openFile(config, "..");
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					Db4o.openServer(config, "..", 0);
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					final ObjectServer server = Db4o.openServer(newInMemoryConfiguration(), "..", 0);
					try {
						server.openClient(config);
					} finally {
						server.close();
					}
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					Db4o.openClient(config, "localhost", 0xdb40, "user", "password");
				}}
			)
		);
		
		testUnits(ConfigurationReuseTestUnit.class);
	}

	static final FixtureVariable<Function4<Configuration, Runnable>> CONFIGURATION_USE_FUNCTION = FixtureVariable.newInstance("Successul configuration use");
	static final FixtureVariable<Procedure4<Configuration>> CONFIGURATION_REUSE_PROCEDURE = FixtureVariable.newInstance("Configuration reuse attempt");
	
	public static class ConfigurationReuseTestUnit implements TestCase {
		
		public void test() {
			final Configuration config = newInMemoryConfiguration();
			final Runnable tearDownBlock = CONFIGURATION_USE_FUNCTION.value().apply(config);
			try {
				Assert.expect(IllegalArgumentException.class, new CodeBlock() {
					public void run() throws Throwable {
						CONFIGURATION_REUSE_PROCEDURE.value().apply(config);
					}
				});
			} finally {
				tearDownBlock.run();
			}
		}

	}
	
	static Configuration newInMemoryConfiguration() {
		final Configuration config = Db4o.newConfiguration();
		config.storage(new MemoryStorage());
		return config;
	}
}