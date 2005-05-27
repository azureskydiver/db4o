/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.test;

import com.db4o.binding.verifier.IVerifier;
import com.db4o.binding.verifiers.reusable.RegularExpressionVerifier;

public class Person {
    String name;
    int age;
    
    public IVerifier getAgeVerifier() {
        return new RegularExpressionVerifier("/^[0-9]*$/", "/^[0-9]{1,3}$/", 
                "Please enter an age between 0 and 999");
    }
}

