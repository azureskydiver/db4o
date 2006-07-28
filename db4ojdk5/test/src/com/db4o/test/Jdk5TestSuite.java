/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.test.*;
import com.db4o.test.jdk5.*;

public class Jdk5TestSuite extends TestSuite{   
    public Class[] tests(){
        return new Class[] {
            Jdk5EnumTest.class,
            Jdk5DeleteEnum.class,
            
            CallConstructors.class,
            EmptyObjectSet.class,
            FulltextIndex.class,
            ObjectSetAsIterator.class,
            ObjectSetAsList.class,
            QueryForClass.class,
            
        };
    }
}