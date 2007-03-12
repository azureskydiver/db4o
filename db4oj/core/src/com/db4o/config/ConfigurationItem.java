/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

import com.db4o.internal.*;


/**
 * Implement this interface for configuration items that are to be applied 
 * to ObjectContainers after they are opened. 
 */
public interface ConfigurationItem {
	
	/**
	 * Implement this interface to to apply a configuration item 
	 * to an ObjectContainerBase after it is opened.
	 * @param objectContainer the ObjectContainerBase
	 */
	public void apply(ObjectContainerBase objectContainer);
	
}
