/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.activation;

/**
 * Activator interface. <br>
 * Activatable objects need to have a reference to 
 * an Activator implementation, which is called
 * by TransparentActivation framework, when a request is
 * received to activate the host object.
 * @see Transparent Activation framework. 
 */
public interface Activator {
	
	/**
	 * Method to be called to activate the host object.
	 */
	void activate();
}
