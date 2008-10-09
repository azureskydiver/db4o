/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.query.*;
import com.db4o.reflect.*;


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

    public ClassMetadata classMetadataForReflectClass(ReflectClass reflectClass);

    public ClassMetadata classMetadataForName(String name);
    
    public ClassMetadata classMetadataForId(int id);

    public HandlerRegistry handlers();
    
    public Config4Impl configImpl();
    
    public Object syncExec(Closure4 block);

    public int instanceCount(ClassMetadata clazz, Transaction trans);
    
    public boolean isClient();

}
