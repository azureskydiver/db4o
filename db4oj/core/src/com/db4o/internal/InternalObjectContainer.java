/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.query.*;


/**
 * @exclude
 */
public interface InternalObjectContainer extends ExtObjectContainer {
    
    public void callbacks(Callbacks cb);
    
    public Callbacks callbacks();
    
    public ObjectContainerBase container();
    
    public Transaction transaction();
    
    public void onCommittedListener();
    
    public NativeQueryHandler getNativeQueryHandler();

}
