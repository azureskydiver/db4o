/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import com.db4o.config.*;
import com.db4o.ext.*;

public abstract class HandlerUpdateTestCaseBase extends FormatMigrationTestCaseBase {
    
    protected String[] versionNames() {
        return new String[]{
            "db4o_6.4.000",
        };
    }

    public static class Holder{
        
        public Object[] _values;
        
        public Object _arrays;
        
    }
    
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
        Holder holder = (Holder) objectContainer.query(Holder.class).next();
        
        assertValues(holder._values);
        
        assertArrays(holder._arrays);
    }

    protected abstract String typeName();

    protected abstract Object[] createValues();
    
    protected abstract Object createArrays();
    
    protected abstract void assertValues(Object[] values);
    
    protected abstract void assertArrays(Object obj);

}
