/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.util.*;

import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @sharpen.ignore
 * @exclude
 */
public class CollectionTypeHandlers {
    
    private final int INSTALLED_FROM_VERSION = 4;
    
    /*
     * The plan is to switch live both changes at once.
     */
    public static boolean enabled(){
        return NullableArrayHandling.enabled();
    }
    
    public void register(Config4Impl config){
        if(! enabled()){
            return;
        }
        registerListTypeHandler(config, ArrayList.class);
    }
    
    private void registerListTypeHandler(Config4Impl config, Class clazz){
        final ReflectClass claxx = config.reflector().forClass(clazz);
        config.registerTypeHandler(new TypeHandlerPredicate() {
            public boolean match(ReflectClass classReflector, int version) {
                if(version < INSTALLED_FROM_VERSION){
                    return false;
                }
                return claxx.equals(classReflector);
            }
        
        },new ListTypeHandler());
    }

}
