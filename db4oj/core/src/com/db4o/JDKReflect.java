/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * 
 * package and class name are hard-referenced in JavaOnly#jdk()
 * 
 * TODO: may need to use this on instead of JDK on .NET. Check!
 * 
 */
class JDKReflect extends JDK {
	
    final boolean methodIsAvailable(
            String className,
            String methodName,
            Class[] params) {
        	
            try {
            	
                Class clazz = Class.forName(className);
                if (clazz.getMethod(methodName, params) !=null) {
                    return true;
                }
                return false;
            } catch (Throwable t) {
            }
            return false;
        }

}
