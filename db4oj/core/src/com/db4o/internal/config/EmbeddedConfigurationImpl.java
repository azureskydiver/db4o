package com.db4o.internal.config;

import com.db4o.config.*;
import com.db4o.internal.*;

public class EmbeddedConfigurationImpl implements EmbeddedConfiguration, LegacyConfigurationProvider {

	private final Config4Impl _legacy;

	public EmbeddedConfigurationImpl(Configuration legacy) {
		_legacy = (Config4Impl) legacy;
    }

	public CacheConfiguration cache() {
		return new CacheConfigurationImpl(_legacy);
	}
	
	public FileConfiguration file() {
		return new FileConfigurationImpl(_legacy);
	}

	public CommonConfiguration common() {
		return new CommonConfigurationImpl(_legacy);
	}

	public Config4Impl legacy() {
		return _legacy;
	}

}
