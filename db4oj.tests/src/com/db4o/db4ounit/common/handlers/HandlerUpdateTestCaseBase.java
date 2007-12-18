/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

public abstract class HandlerUpdateTestCaseBase extends FormatMigrationTestCaseBase {
    
    public static class Holder{
        
        public Object[] _values;
        
        public Object _arrays;
        
    }
    
    protected int _handlerVersion;
    
    protected String fileNamePrefix() {
        return "migrate_" + typeName() + "_" ;
    }
    
    protected String[] versionNames() {
    	return new String[] { Db4o.version().substring(5) };
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
        _handlerVersion = VersionServices.slotHandlerVersion(objectContainer, obj);
    }

    protected abstract String typeName();

    protected abstract Object[] createValues();
    
    protected abstract Object createArrays();
    
    protected abstract void assertValues(Object[] values);
    
    protected abstract void assertArrays(Object obj);

    protected int[] castToIntArray(Object obj){
        ObjectByRef byRef = new ObjectByRef(obj);
        castToIntArrayJavaOnly(byRef);
        return (int[]) byRef.value;
    }

    /**
     * @sharpen.remove
     */
    private void castToIntArrayJavaOnly(ObjectByRef byRef) {
        if(_db4oHeaderVersion != VersionServices.HEADER_30_40){
            return;
        }
            
        // Bug in the oldest format: 
        // It accidentally converted int[] arrays to Integer[] arrays.
        
        Integer[] wrapperArray = (Integer[])byRef.value;
        int[] res = new int[wrapperArray.length];
        for (int i = 0; i < wrapperArray.length; i++) {
            if(wrapperArray[i] != null){
                res[i] = wrapperArray[i].intValue();
            }
        }
        byRef.value = res;
    }
    
    /**
     * On .NET there are no primitive wrappers, so the primitives have 
     * their default value. Since default values are tested OK with the 
     * other values test, we don't have to test again, so it's safe to: 
     * @sharpen.remove
     */
    protected void assertPrimitiveWrapperIsNullJavaOnly(Object obj) {
        Assert.isNull(obj);
    }

}
