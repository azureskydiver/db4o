package com.db4o.tools;

import com.db4o.ObjectContainer;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.foundation.StopWatch;

/**
 * Keeps track of query statistics.
 */
public class QueryStats {	
	
	private EventRegistry _registry = null;
	
	private int _activationCount;
	
	private final StopWatch _watch = new StopWatch();
	
	private final EventListener4 _queryStarted = new EventListener4() {			
		public void onEvent(Event4 e, EventArgs args) {
			_activationCount = 0;
			_watch.start();
		}			
	};
	
	private final EventListener4 _queryFinished = new EventListener4() {
		public void onEvent(Event4 e, EventArgs args) {
			_watch.stop();
		}
	};
	
	private final EventListener4 _activated = new EventListener4() {
		public void onEvent(Event4 e, EventArgs args) {
			++_activationCount;
		};
	};
	
	/**
	 * How long the last query took to execute.
	 * 
	 * @return time in miliseconds
	 */
	public long executionTime() {
		return _watch.elapsed();
	}
	
	/**
	 * How many objects were activated so far.
	 */
	public int activationCount() {
		return _activationCount;
	}

	/**
	 * Starts gathering query statistics for the specified container.
	 */
	public void connect(ObjectContainer container) {
		if (_registry != null) {
			throw new IllegalArgumentException("Already connected to an ObjectContainer");
		}
		_registry = EventRegistryFactory.forObjectContainer(container);
		_registry.queryStarted().addListener(_queryStarted);
		_registry.queryFinished().addListener(_queryFinished);
		_registry.activated().addListener(_activated);
	}
	
	/**
	 * Disconnects from the current container.
	 */
	public void disconnect() {
		if (null != _registry) {
			_registry.queryStarted().removeListener(_queryStarted);
			_registry.queryFinished().removeListener(_queryFinished);
			_registry.activated().removeListener(_activated);
			_registry = null;
		}
	}
}