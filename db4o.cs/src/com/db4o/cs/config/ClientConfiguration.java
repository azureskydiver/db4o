/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.config;

import com.db4o.config.*;
import com.db4o.messaging.*;

/**
 * Configuration interface for db4o clients.
 * @since 7.5
 */
public interface ClientConfiguration extends NetworkingConfigurationProvider, CommonConfigurationProvider {
	
	/**
	 * Sets the number of IDs to be pre-allocated in the database for new 
	 * objects created on the client.
	 * This setting should be used on the client side. In embedded mode this setting
	 * has no effect.
	 * @param prefetchIDCount
	 *            The number of IDs to be prefetched
	 *            
	 * @sharpen.property
	 */
	void prefetchIDCount(int prefetchIDCount);

	/**
	 * Sets the number of objects to be prefetched for an ObjectSet in C/S mode.
	 * This setting should be used on the server side. In embedded mode this setting
	 * has no effect.
	 * @param prefetchObjectCount
	 *            The number of objects to be prefetched
	 *            
	 * @sharpen.property
	 */
	void prefetchObjectCount(int prefetchObjectCount);
	
	/**
	 * returns the MessageSender for this Configuration context.
	 * This setting should be used on the client side.
	 * @return MessageSender
	 * 
	 * @sharpen.property
	 */
	public MessageSender messageSender();


}
