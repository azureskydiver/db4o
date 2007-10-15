/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.events;

import com.db4o.events.*;
import com.db4o.cs.events.*;
import com.db4o.internal.*;
import com.db4o.internal.events.*;


/**
 * @exclude
 */
public class ClientEventRegistryImpl implements ClientEventRegistry, ClientEventCallbacks{
    
    protected final Event4Impl _clientSocketReadTimeout = new Event4Impl();


    public boolean continueOnTimeout(Transaction transaction) {
        return EventPlatform.triggerCancellableObjectEventArgs(transaction, _clientSocketReadTimeout, null);
    }

    public Event4 clientSocketReadTimeout() {
        return _clientSocketReadTimeout;
    }

}
