/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.events;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.cs.events.*;


/**
 * Provides an interface for getting a {@link ClientEventRegistry} from an 
 * {@link ObjectContainer}. 
 */
public class ClientEventRegistryFactory {
    
    /**
     * Returns an {@link ClientEventRegistry} for registering events with the 
     * specified client {@link ObjectContainer}.
     */
    public static ClientEventRegistry forClient(ObjectContainer objectContainer) {
        if (null == objectContainer) {
            throw new ArgumentNullException();
        }
        ClientObjectContainer container = ((ClientObjectContainer)objectContainer);
        ClientEventCallbacks callbacks = container.clientEventCallbacks();
        if (callbacks instanceof ClientEventRegistry) {
            return (ClientEventRegistry)callbacks;
        }       
        if (callbacks instanceof NullClientEventCallbacks) {
            ClientEventRegistryImpl impl = new ClientEventRegistryImpl();
            container.clientEventCallbacks(impl);
            return impl;
        }
        throw new IllegalArgumentException();
    }

}
