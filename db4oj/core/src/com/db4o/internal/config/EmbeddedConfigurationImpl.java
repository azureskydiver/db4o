package com.db4o.internal.config;

import com.db4o.config.*;
import com.db4o.internal.*;

public class EmbeddedConfigurationImpl implements EmbeddedConfiguration, LegacyConfigurationProvider {

	private final Config4Impl _legacy;

	public EmbeddedConfigurationImpl(Configuration legacy) {
		_legacy = (Config4Impl) legacy;
    }

	public LocalConfiguration local() {
		return new LocalConfigurationImpl(_legacy);
	}

	public BaseConfiguration base() {
		return new BaseConfigurationImpl(_legacy);
	}

	public Config4Impl legacy() {
		return _legacy;
	}

}
