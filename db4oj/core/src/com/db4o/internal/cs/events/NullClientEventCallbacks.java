/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.events;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class NullClientEventCallbacks implements ClientEventCallbacks{

    public boolean continueOnTimeout(Transaction transaction) {
        return true;
    }

}
