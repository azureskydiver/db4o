/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
public class Handlers {
    
    public static boolean handlesSimple(TypeHandler4 handler){
        if  ((handler instanceof PrimitiveHandler) ||  (handler instanceof StringHandler)){
            return true;
        }
        if(handler instanceof ArrayHandler){
            return handlesSimple( ((ArrayHandler)handler).i_handler);
        }
        return false;
    }
    
    public static boolean handlesClass(TypeHandler4 handler){
        if  ((handler instanceof ClassMetadata)){
            return true;
        }
        if(handler instanceof ArrayHandler){
            return handlesClass(((ArrayHandler)handler).i_handler);
        }
        return false;
    }

    

}
