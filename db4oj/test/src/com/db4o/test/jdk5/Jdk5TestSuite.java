package com.db4o.test.jdk5;

import com.db4o.test.*;

public class Jdk5TestSuite extends TestSuite{   
    public Class[] tests(){
        return new Class[] {
            Jdk5EnumTest.class,
            Jdk5DeleteEnum.class
        };
    }
}