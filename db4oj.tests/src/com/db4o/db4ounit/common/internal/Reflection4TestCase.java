/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.util.*;


public class Reflection4TestCase implements TestCase{
    
    private static int _staticInt;
    
    public void testInvokeStatic(){
        _staticInt = 0;
        String methodName = "setStaticIntToOne";
        if(PlatformInformation.isDotNet()){
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        }
        Reflection4.invokeStatic(this.getClass().getName(), methodName );
        Assert.areEqual(1, _staticInt);
    }
    
    public static void setStaticIntToOne(){
        _staticInt = 1;
    }


}
