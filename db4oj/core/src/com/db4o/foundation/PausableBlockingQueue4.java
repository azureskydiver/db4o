/* Copyright (C) 2010 Versant Inc. http://www.db4o.com */
package com.db4o.foundation;

public interface PausableBlockingQueue4<T> extends BlockingQueue4<T> {

	void pause();

	void resume();

	boolean isPaused();

	/**
	 * <p>
	 * Returns the next element in queue if there is one available, returns null
	 * otherwise.
	 * <p>
	 * This method will not never block, regardless of the queue being paused or
	 * no elements are available.
	 * 
	 * @return next element, if available and queue not paused.
	 */
	T tryNext();
	
}
