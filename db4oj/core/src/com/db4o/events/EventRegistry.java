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
}

