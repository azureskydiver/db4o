/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

public abstract class HandlerUpdateTestCaseBase extends FormatMigrationTestCaseBase {
    
    protected String[] versionNames() {
        return new String[]{
                /*"db4o_3.0.3",
                "db4o_4.0.010",
                "db4o_4.6.010",
                "db4o_5.0.018",
                "db4o_5.3.001",*/
                "db4o_5.4.012",
                "db4o_5.5.1",
                "db4o_5.6.000",
                "db4o_5.7.003",
                "db4o_6.0.200",
                "db4o_6.1.604",
                "db4o_6.3.500",
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
        Query q = objectContainer.query();
        q.constrain(Holder.class);
        ObjectSet objectSet = q.execute();
        Holder holder = (Holder) objectSet.next();
        
        assertValues(holder._values);
        
        assertArrays(holder._arrays);
    }

    protected abstract String typeName();

    protected abstract Object[] createValues();
    
    protected abstract Object createArrays();
    
    protected abstract void assertValues(Object[] values);
    
    protected abstract void assertArrays(Object obj);

}
