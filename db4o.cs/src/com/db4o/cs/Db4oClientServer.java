package com.db4o.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.config.*;

/**
 * Factory class to open db4o servers and to connect db4o clients
 * to them. 
 * @since 7.5
 */
public class Db4oClientServer {

	
	/**
	 * creates a new {@link ServerConfiguration}
	 */
	public static ServerConfiguration newServerConfiguration() {
		return new ServerConfigurationImpl(newLegacyConfig());
	}

	/**
	 * opens a db4o server with the specified configuration on
	 * the specified database file and provides access through
	 * the specified port.
	 */
	public static ObjectServer openServer(ServerConfiguration config,
			String databaseFileName, int port) {
		final Config4Impl legacy = legacyFrom(config);
		return legacy.clientServerFactory().openServer(legacy, databaseFileName, port, new PlainSocketFactory());
	}

	/**
	 * opens a db4o client instance with the specified configuration.
	 * @param config the configuration to be used
	 * @param host the host name of the server that is to be connected to
	 * @param port the server port to connect to
	 * @param user the username for authentication
	 * @param password the password for authentication
	 * @see #openServer(ServerConfiguration, String, int)
	 * @see ObjectServer#grantAccess(String, String)
	 */
	public static ObjectContainer openClient(ClientConfiguration config,
			String host, int port, String user, String password) {
		final Config4Impl legacy = legacyFrom(config);
		return legacy.clientServerFactory().openClient(legacy, host, port, user, password, new PlainSocketFactory());
	}
	
	private static Config4Impl legacyFrom(NetworkingConfigurationProvider config) {
		return ((NetworkingConfigurationImpl)config.networking()).config();
	}
	
	/**
	 * creates a new {@link ClientConfiguration} 
	 */
	public static ClientConfiguration newClientConfiguration() {
		return new ClientConfigurationImpl(newLegacyConfig());
	}
	
    private static Config4Impl newLegacyConfig() {
		return (Config4Impl) Db4o.newConfiguration();
	}
}
