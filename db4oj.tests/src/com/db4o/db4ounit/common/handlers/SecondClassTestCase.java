/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.util.*;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class SecondClassTestCase extends AbstractDb4oTestCase{
    
    
    static Hashtable4 objectIsSecondClass;
    
    public static class Item{
        
    }
    
    public static class CustomSecondClassItem{
        
    }
    
    public static class CustomFirstClassItem{
        
    }

    
    static{
        objectIsSecondClass = new Hashtable4();
        register(new Integer(1), true);
        register(new Date(), true);
        register("astring", true);
        register(new Item(), false);
        register(new int[] {1}, false);
        register(new Date[] {new Date()}, false);
        register(new Item[] {new Item()}, false);
        register(new CustomFirstClassItem(), false);
        register(new CustomSecondClassItem(), true);
    }

    private static void register(Object obj, boolean isSecondClass) {
        objectIsSecondClass.put(obj, new Boolean(isSecondClass));
    }
    
    public static class FirstClassTypeHandler extends FirstClassObjectHandler{
        
    }
    
    public static class SecondClassTypeHandler extends FirstClassObjectHandler implements EmbeddedTypeHandler{
        
    }

    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(CustomFirstClassItem.class), 
            new FirstClassTypeHandler());
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(CustomSecondClassItem.class), 
            new SecondClassTypeHandler());
    }
    
    protected void store() throws Exception {
        store(new Item());
        store(new CustomFirstClassItem());
        store(new CustomSecondClassItem());
        
    }
    
    public void test(){
        Iterator4 i = objectIsSecondClass.keys();
        while(i.moveNext()){
            Object currentObject = i.current();
            boolean isSecondClass = ((Boolean)objectIsSecondClass.get(currentObject)).booleanValue();
            ClassMetadata classMetadata = container().classMetadataForObject(currentObject);
            Assert.areEqual(isSecondClass, classMetadata.isSecondClass());
        }
        
        
        
    }

}
