/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.events;

import com.db4o.events.*;


/**
 * Provides the interface to register event handlers for client  
 * {@link com.db4o.ObjectContainer} events.<br>
 * EventRegistry methods represent events available for registering callbacks.
 * An EventRegistry instance can be obtained from the {@link ClientEventRegistryFactory}.
 * <code>ClientEventRegistry registry =  ClientEventRegistryFactory.forClient(container);</code>
 * A new callback can be registered for an event with code like the following:
 * <code>registry.clientSocketReadTimeout().addListener(new EventListener4(){...});</code>
 * @see ClientEventRegistryFactory
 * @see EventListener4
 */
public interface ClientEventRegistry {
    
    /**
     * This event is fired when the client socket runs into read
     * timeouts. 
     * This may happen when the server is busy while the 
     * client is sending C/S messages.<br><br>
     * The default behaviour is to continue keeping up the connection
     * on timeouts and to retry an infinite number of times. Use this 
     * event to provide own logic for closing connections by calling 
     * #cancel() on the CancellableEventArgs parameter.<br><br>
     * An example:
     * <CODE>
     * ClientEventRegistryFactory.forClient(objectContainer).clientSocketReadTimeout().addListener(new EventListener4() {
     *     public void onEvent(Event4 e, EventArgs args) {
     *         if(tooMuchTimepassedOrTooManyRetries()){
     *             ((CancellableEventArgs) args).cancel();
     *         }
     *     }
     * });
     * </CODE>
     * @sharpen.event com.db4o.events.CancellableObjectEventArgs
     * @return
     */
    Event4 clientSocketReadTimeout();

}
