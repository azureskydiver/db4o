package com.db4o.drs.versant.eventlistener;

import com.db4o.foundation.*;

public class PausableBlockingQueue<T> extends BlockingQueue<T> {

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

}
