package com.db4o.foundation;

public interface BlockingQueue4<T> extends Queue4<T> {
	
	/**
	 * <p>Returns the next queued item or waits for it to be available for the maximum of <code>timeout</code> miliseconds.
	 * @param timeout maximum time to wait for the next avilable item in miliseconds
	 * @return the next item or <code>null</code> if <code>timeout</code> is reached
	 * @throws BlockingQueueStoppedException if the {@link BlockingQueue4#stop()} is called.
	 */
	T next(final long timeout) throws BlockingQueueStoppedException;
	
	void stop();

}
