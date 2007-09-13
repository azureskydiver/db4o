/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.query.*;

public abstract class HandlerUpdateTestCaseBase extends FormatMigrationTestCaseBase {
    
    public static String[] db4oVersions = new String[]{
            "db4o_6.4.000",
    };
    protected String[] versionNames() {
        return db4oVersions;
    }

    public static class Holder{
        
        public Object[] _values;
        
        public Object _arrays;
        
    }
    
    protected int _handlerVersion;
    
    protected String fileNamePrefix() {
        return "migrate_" + typeName() + "_" ;
    }

    protected void configure(Configuration config) {
        // no special configuration
    }
    
    protected void store(ExtObjectContainer objectContainer) {
        Holder holder = new Holder();
        
        holder._values = createValues();
        
        holder._arrays = createArrays();
        objectContainer.set(holder);
    }
    
    protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
        Query q = objectContainer.query();
        q.constrain(Holder.class);
        ObjectSet objectSet = q.execute();
        Holder holder = (Holder) objectSet.next();
        
        investigateHandlerVersion(objectContainer, holder);
        
        assertValues(holder._values);
        assertArrays(holder._arrays);
    }
    
    private void investigateHandlerVersion(ExtObjectContainer objectContainer, Object obj){
        int id = (int) objectContainer.getID(obj);
        ObjectInfo objectInfo = objectContainer.getObjectInfo(obj);
        ObjectContainerBase container = (ObjectContainerBase) objectContainer;
        Transaction trans = container.transaction();
        Buffer buffer = container.readReaderByID(trans, id);
        UnmarshallingContext context = new UnmarshallingContext(trans, (ObjectReference)objectInfo, Const4.TRANSIENT, false);
        context.buffer(buffer);
        context.persistentObject(obj);
        context.activationDepth(0);
        context.read();
        _handlerVersion = context.handlerVersion();
    }

    protected abstract String typeName();

    protected abstract Object[] createValues();
    
    protected abstract Object createArrays();
    
    protected abstract void assertValues(Object[] values);
    
    protected abstract void assertArrays(Object obj);

}
