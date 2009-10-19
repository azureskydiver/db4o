/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */
/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.optional.monitoring.samples;

import com.db4o.config.*;
import com.db4o.monitoring.*;

/**
 * @sharpen.remove 
 */
public class AllMonitoringSupport {
	
	@decaf.RemoveFirst
	public void apply(CommonConfigurationProvider config){
		addMonitoringSupport(config.common());
	}
	
	@decaf.Ignore
	private void addMonitoringSupport(CommonConfiguration commonConfig) {
		commonConfig.add(new IOMonitoringSupport());
		commonConfig.add(new QueryMonitoringSupport());
		commonConfig.add(new NativeQueryMonitoringSupport());
		commonConfig.add(new ReferenceSystemMonitoringSupport());
		commonConfig.add(new FreespaceMonitoringSupport());
		commonConfig.add(new com.db4o.monitoring.cs.NetworkingMonitoringSupport());
		commonConfig.add(new ObjectLifecycleMonitoringSupport());
	}
	
}
