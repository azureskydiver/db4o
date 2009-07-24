/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.threading;

import java.util.*;

import com.db4o.events.*;
import com.db4o.internal.events.*;

public class ThreadPool4Impl implements ThreadPool4 {

	private final Event4Impl<UncaughtExceptionEventArgs> _uncaughtException = Event4Impl.newInstance();
	private final List<Thread> _activeThreads = new ArrayList<Thread>();

	public void join(int timeoutMilliseconds) throws InterruptedException {
		for (Thread thread : activeThreads()) {
			thread.join(timeoutMilliseconds);
		}
	}

	public void startLowPriority(Runnable task) {
		final Thread thread = threadFor(task);
		setLowPriorityOn(thread);
		activateThread(thread);
	}

	/**
	 * @sharpen.remove
	 */
	private void setLowPriorityOn(final Thread thread) {
	    thread.setPriority(Thread.MIN_PRIORITY);
    }

	public void start(final Runnable task) {
		final Thread thread = threadFor(task);
		activateThread(thread);
    }

	private Thread threadFor(final Runnable task) {
	    final Thread thread = new Thread(new Runnable() {
        	public void run() {
        		try {
        			task.run();
        		} catch (Throwable e) {
        			triggerUncaughtExceptionEvent(e);
        		} finally {
        			dispose(Thread.currentThread());
        		}
            }
        });
	    thread.setDaemon(true);
		return thread;
    }

	private void activateThread(final Thread thread) {
	    thread.start();
	    addActiveThread(thread);
    }

	private Thread[] activeThreads() {
		synchronized (_activeThreads) {
			return _activeThreads.toArray(new Thread[_activeThreads.size()]);
		}
	}
	
	private void addActiveThread(final Thread thread) {
	    synchronized (_activeThreads) {
	    	_activeThreads.add(thread);
        }
    }

	protected void dispose(Thread thread) {
		synchronized (_activeThreads) {
			_activeThreads.remove(thread);
		}
	}

	protected void triggerUncaughtExceptionEvent(Throwable e) {
		_uncaughtException.trigger(new UncaughtExceptionEventArgs(e));
    }

	public Event4<UncaughtExceptionEventArgs> uncaughtException() {
	    return _uncaughtException;
    }

}
