/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.cs.optional.monitoring;

import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.foundation.*;
import com.db4o.cs.internal.config.*;
import com.db4o.internal.*;

/**
 * publishes statistics about networking activities to JMX.
 */
@decaf.Ignore
public class NetworkingMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
	}

	public void prepare(Configuration configuration) {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(configuration);
		Socket4Factory currentSocketFactory = networkConfig.socketFactory();
		networkConfig.socketFactory(new MonitoredSocket4Factory(currentSocketFactory));
	}
}
