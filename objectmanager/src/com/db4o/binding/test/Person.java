/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.test;

import com.db4o.binding.verifier.IVerifier;
import com.db4o.binding.verifiers.reusable.RegularExpressionVerifier;

public class Person {
    String name;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person() {
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    
    int age;

    /**
     * @return Returns the age.
     */
    public int getAge() {
        return age;
    }
    
    /**
     * @param age The age to set.
     */
    public void setAge(int age) {
        this.age = age;
    }
    
    public IVerifier getAgeVerifier() {
        return new RegularExpressionVerifier("/^[0-9]*$/", "/^[0-9]{1,3}$/", 
                "Please enter an age between 0 and 999");
    }
}

