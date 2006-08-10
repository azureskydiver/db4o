/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public interface EventRegistry {
	
	/**
	 * Receives {@link QueryEventArgs}.
	 * 
	 * @event QueryEventArgs
	 * @return
	 */
	public Event4 queryStarted();
	
	/**
	 * Receives {@link QueryEventArgs}.
	 *
	 * @event QueryEventArgs
	 * @return
	 */
	public Event4 queryFinished();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 * 
	 * @event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 creating();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 *
	 * @event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 activating();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 *
	 * @event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 updating();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 deleting();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @event CancellableObjectEventArgs
	 * @return
	 */
	public Event4 deactivating();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @event ObjectEventArgs
	 * @return
	 */
	public Event4 activated();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @event ObjectEventArgs
	 * @return
	 */
	public Event4 created();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @event ObjectEventArgs
	 * @return
	 */
	public Event4 updated();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @event ObjectEventArgs
	 * @return
	 */
	public Event4 deleted();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @event ObjectEventArgs
	 * @return
	 */
	public Event4 deactivated();
}
