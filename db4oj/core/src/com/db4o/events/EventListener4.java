/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

/**
 * A listener for {@link Event4} events.
 *
 * @see Event4
 * @sharpen.ignore
 */
public interface EventListener4 {
	
	/**
	 * The event was triggered.
	 * 
	 * @param e the specific event that was triggered
	 * @param args the arguments for the specific event
	 */
	public void onEvent(Event4 e, EventArgs args);
}
