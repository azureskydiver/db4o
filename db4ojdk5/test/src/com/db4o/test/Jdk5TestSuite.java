package com.db4o.test;

import com.db4o.test.*;
import com.db4o.test.jdk5.*;

public class Jdk5TestSuite extends TestSuite{   
    public Class[] tests(){
        return new Class[] {
            Jdk5EnumTest.class,
            Jdk5DeleteEnum.class,
            
            ObjectSetAsIterator.class,
            ObjectSetAsList.class,
            CallConstructors.class,
            FulltextIndex.class
        };
    }
}