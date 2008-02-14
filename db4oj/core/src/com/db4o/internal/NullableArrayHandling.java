/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;


/**
 * @exclude
 */
public class NullableArrayHandling {
    
    private static boolean enabled = false;

    public static boolean useJavaHandling() {
        if(Deploy.csharp){
            return enabled;
        }
        return true;
    }
    
    public static boolean useOldNetHandling() {
        return ! useJavaHandling();
    }

    public static boolean disabled() {
        return ! enabled;
    }

}
