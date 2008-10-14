/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.config;

import com.db4o.config.*;
import com.db4o.messaging.*;

/**
 * Configuration interface for db4o servers.
 * @since 7.5
 */
public interface ServerConfiguration extends FileConfigurationProvider, NetworkingConfigurationProvider, CommonConfigurationProvider {
	
	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * This setting should be used on the server side.<br><br>
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 *            
	 * @sharpen.property
	 */
	void messageRecipient(MessageRecipient messageRecipient);

}
