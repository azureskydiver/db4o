/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class CollectionTypeHandlerRegistry {
    
    private final Config4Impl _config;
    
    private final TypeHandler4 _listTypeHandler;
    
    public CollectionTypeHandlerRegistry(Config4Impl config, TypeHandler4 listTypeHandler){
        _config = config;
        _listTypeHandler = listTypeHandler;
    }
    
    /*
     * The plan is to switch live both changes at once.
     */
    public static boolean enabled(){
        return NullableArrayHandling.enabled();
    }
    
    public void registerCollection(Class clazz){
        if(! enabled()){
            return;
        }
        registerListTypeHandlerFor(clazz);    
    }
    
    public void ignoreFieldsOn(Class clazz){
    	if(! enabled()){
            return;
        }
    	_config.registerTypeHandler(new SingleClassTypeHandlerPredicate(clazz), new IgnoreFieldsTypeHandler());
    }
    
    private void registerListTypeHandlerFor(Class clazz){
        _config.registerTypeHandler(new SingleClassTypeHandlerPredicate(clazz), _listTypeHandler);
    }

}
