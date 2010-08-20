/* Copyright (C) 2010 Versant Inc. http://www.db4o.com */
package com.db4o.foundation;

public class PausableBlockingQueue<T> extends BlockingQueue<T> implements PausableBlockingQueue4<T> {

	private volatile boolean _paused = false;

	public void pause() {
		_paused = true;
	}

	public synchronized void resume() {
		_paused = false;
		notify();
	}

	@Override
	public T next() throws BlockingQueueStoppedException {
		waitForNext();
		if (_paused) {
			synchronized (this) {
				if (_paused) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		return super.next();
	}

	@Override
	public T next(long timeout) throws BlockingQueueStoppedException {
		if (!waitForNext(timeout)) {
			return null;
		}
		if (_paused) {
			synchronized (this) {
				if (_paused) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		return super.next(timeout);
	}

	@Override
	public void stop() {
		if (_paused) {
			synchronized (this) {
				if (_paused) {
					notifyAll();
				}
			}
		}
		super.stop();
	}

	public boolean isPaused() {
		return _paused;
	}

	public T tryNext() {
		return _lock.run(new Closure4<T>() {
			public T run() {
				return isPaused() ? null : hasNext() ? next() : null;
			}
		});
	}

}
