/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test.types
{
	public class Person
	{
        public String firstName;
        public String lastName;

        public Person(){}
    
        public Person(String firstName, String lastName){
            this.firstName = firstName;
            this.lastName = lastName;
        }
    
        public override String ToString(){
            return "Person " + firstName + " " + lastName;
        }

    }
}
