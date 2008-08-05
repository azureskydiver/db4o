/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class TypeHandlerConfiguration {
    
    protected final Config4Impl _config;
    
    private TypeHandler4 _listTypeHandler;
    
    private TypeHandler4 _mapTypeHandler;
    
    public abstract void apply();
    
    public TypeHandlerConfiguration(Config4Impl config){
        _config = config;
    }
    
    protected void listTypeHandler(TypeHandler4 listTypeHandler){
    	_listTypeHandler = listTypeHandler;
    }
    
    protected void mapTypeHandler(TypeHandler4 mapTypehandler){
    	_mapTypeHandler = mapTypehandler;
    }
    
    /*
     * The plan is to switch live both changes at once.
     */
    public static boolean enabled(){
        return NullableArrayHandling.enabled();
    }
    
    protected void registerCollection(Class clazz){
        registerListTypeHandlerFor(clazz);    
    }
    
    protected void registerMap(Class clazz){
        registerMapTypeHandlerFor(clazz);    
    }

    
    protected void ignoreFieldsOn(Class clazz){
    	_config.registerTypeHandler(new SingleClassTypeHandlerPredicate(clazz), new IgnoreFieldsTypeHandler());
    }
    
    private void registerListTypeHandlerFor(Class clazz){
        registerTypeHandlerFor(_listTypeHandler, clazz);
    }
    
    private void registerMapTypeHandlerFor(Class clazz){
        registerTypeHandlerFor(_mapTypeHandler, clazz);
    }
    
    private void registerTypeHandlerFor(TypeHandler4 typeHandler, Class clazz){
        _config.registerTypeHandler(new SingleClassTypeHandlerPredicate(clazz), typeHandler);
    }

}
