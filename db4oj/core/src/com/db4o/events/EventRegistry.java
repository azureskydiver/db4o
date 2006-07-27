/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public interface EventRegistry {
	
	/**
	 * Receives {@link QueryEventArgs}.
	 * 
	 * @return
	 */
	public Event4 queryStarted();
	
	/**
	 * Receives {@link QueryEventArgs}.
	 * 
	 * @return
	 */
	public Event4 queryFinished();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 * 
	 * @return
	 */
	public Event4 objectCanNew();

	/**
	 * Receives {@link CancellableObjectEventArgs}.
	 * 
	 * @return
	 */
	public Event4 objectCanActivate();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectCanUpdate();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectCanDelete();
	
	/**
	 * Receives {@link CancellableObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectCanDeactivate();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @return
	 */
	public Event4 objectOnActivate();

	/**
	 * Receives {@link ObjectEventArgs}.
	 * 
	 * @return
	 */
	public Event4 objectOnNew();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectOnUpdate();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectOnDelete();

	/**
	 * Receives {@link ObjectEventArgs}
	 * 
	 * @return
	 */
	public Event4 objectOnDeactivate();
}
