/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.config;

import com.db4o.config.*;

/**
 * Configuration interface for db4o servers.
 * @since 7.5
 */
public interface ServerConfiguration extends FileConfigurationProvider, NetworkingConfigurationProvider, CommonConfigurationProvider , CacheConfigurationProvider{

}
