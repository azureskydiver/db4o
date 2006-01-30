package com.db4o.inside.replication;

import com.db4o.inside.traversal.CollectionFlattener;
import com.db4o.reflect.ReflectClass;

public interface CollectionHandler extends CollectionFlattener {

    Object cloneWithCounterparts(Object original, ReflectClass claxx, CounterpartFinder finder);
    
    void copyState(Object original, Object dest, CounterpartFinder finder);

}
