/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.config;

/**
 * Interface to configure the IdSystem.
 */
public interface IdSystemConfiguration {
	
	/**
	 * configures db4o to store IDs as pointers.
	 */
	public void usePointerBasedSystem();
	
	/**
	 * configures db4o to use a BTree based ID system.
	 */
	public void useBTreeSystem();

}
