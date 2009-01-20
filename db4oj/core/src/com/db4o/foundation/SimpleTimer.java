/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public final class SimpleTimer implements Runnable {

	private final Runnable _runnable;

	private final int _interval;

	private final String _name;

	private Lock4 _lock;

	public volatile boolean stopped = false;

	public SimpleTimer(Runnable runnable, int interval, String name) {
		_runnable = runnable;
		_interval = interval;
		_name = name;
		_lock = new Lock4();
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName(_name);
		thread.start();
	}

	public void stop() {
		stopped = true;
		
		_lock.run(new Closure4() { 
			public Object run() {
				_lock.awake();
				return null;
				}
		});
	}

	public void run() {
		while (!stopped) {
			_lock.run(new Closure4() { 
				public Object run() {
					_lock.snooze(_interval);
					return null;
				}
			});

			if (!stopped) {
				_runnable.run();
			}
		}
	}
}
