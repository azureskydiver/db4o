package com.db4o.reflect.core;

import com.db4o.reflect.*;

public interface ConstructorAwareReflectClass extends ReflectClass {
    /**
     * instructs to install or uninstall a special constructor for the 
     * respective platform that avoids calling the constructor for the
     * respective class 
     * @param flag true to try to install a special constructor, false if
     * such a constructor is to be removed if present
     * @param testConstructor true, if the special constructor shall be tested, false if it shall be set without testing
     * @return true if the special constructor is in place after the call
     */
    public boolean skipConstructor(boolean flag, boolean testConstructor);
}
