/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

/**
 * Provides a way to register event handlers for specific {@link ObjectContainer} events.
 * 
 * @see EventRegistryFactory
 */
public interface EventRegistry {
	
	/**
	 * Receives {@link QueryEventArgs}.
	 * 
	 * @sharpen.event QueryEventArgs
	 * @return
	 */
	public Event4 queryStarted();
	
	/**
	 * Receives {@link QueryEventArgs}.
	 *
	 * @sharpen.event QueryEventArgs
	 * @return
	 */
	public Event4 queryFinished();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 * 
	 * @sharpen.event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 creating();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 *
	 * @sharpen.event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 activating();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 *
	 * @sharpen.event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 updating();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @sharpen.event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 deleting();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @sharpen.event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 deactivating();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @sharpen.event ObjectEventArgs
	 * @return
	 */
	public Event4 activated();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @sharpen.event ObjectEventArgs
	 * @return
	 */
	public Event4 created();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @sharpen.event ObjectEventArgs
	 * @return
	 */
	public Event4 updated();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @sharpen.event ObjectEventArgs
	 * @return
	 */
	public Event4 deleted();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @sharpen.event ObjectEventArgs
	 * @return
	 */
	public Event4 deactivated();
}
