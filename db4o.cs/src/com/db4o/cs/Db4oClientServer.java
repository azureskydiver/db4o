package com.db4o.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.config.*;

/**
 * @since 7.5
 */
public class Db4oClientServer {

	public static ServerConfiguration newServerConfiguration() {
		return new ServerConfigurationImpl(newLegacyConfig());
	}

	public static ObjectServer openServer(ServerConfiguration config,
			String databaseFileName, int port) {
		final Config4Impl legacy = legacyFrom(config);
		return legacy.clientServerFactory().openServer(legacy, databaseFileName, port, new PlainSocketFactory());
	}

	public static ObjectContainer openClient(ClientConfiguration config,
			String host, int port, String user, String password) {
		final Config4Impl legacy = legacyFrom(config);
		return legacy.clientServerFactory().openClient(legacy, host, port, user, password, new PlainSocketFactory());
	}
	
	private static Config4Impl legacyFrom(NetworkingConfigurationProvider config) {
		return ((NetworkingConfigurationImpl)config.networking()).config();
	}
	
	public static ClientConfiguration newClientConfiguration() {
		return new ClientConfigurationImpl(newLegacyConfig());
	}
	
	@SuppressWarnings("deprecation")
    private static Config4Impl newLegacyConfig() {
		return (Config4Impl) Db4o.newConfiguration();
	}
}
