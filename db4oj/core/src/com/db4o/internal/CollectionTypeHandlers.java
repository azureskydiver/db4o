/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @sharpen.ignore
 * @exclude
 */
public class CollectionTypeHandlers {
    
    private final int INSTALLED_FROM_VERSION = 4;
    
    private final Config4Impl _config;
    
    private final TypeHandler4 _listTypeHandler;
    
    public CollectionTypeHandlers(Config4Impl config, TypeHandler4 listTypeHandler){
        _config = config;
        _listTypeHandler = listTypeHandler;
    }
    
    /*
     * The plan is to switch live both changes at once.
     */
    public static boolean enabled(){
        return NullableArrayHandling.enabled();
    }
    
    public void registerLists(Class[] classes){
        if(! enabled()){
            return;
        }
        for (int i = 0; i < classes.length; i++) {
            registerListTypeHandler(classes[i]);    
        }
    }
    
    private void registerListTypeHandler(Class clazz){
        final ReflectClass claxx = _config.reflector().forClass(clazz);
        _config.registerTypeHandler(new TypeHandlerPredicate() {
            public boolean match(ReflectClass classReflector, int version) {
                if(version < INSTALLED_FROM_VERSION){
                    return false;
                }
                return claxx.equals(classReflector);
            }
        
        },_listTypeHandler);
    }

}
