/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.activation;

/**
 * Activator interface.<br>
 * <br><br>Activatable objects need to have a reference to 
 * an Activator implementation, which is called
 * by Transparent Activation, when a request is received to 
 * activate the host object.
 * @see <a href="http://developer.db4o.com/resources/view.aspx/reference/Object_Lifecycle/Activation/Transparent_Activation_Framework">Transparent Activation framework.</a> 
 */
public interface Activator {
	
	/**
	 * Method to be called to activate the host object.
	 */
	void activate();
}
