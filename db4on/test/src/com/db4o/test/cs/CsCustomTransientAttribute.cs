/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test.cs
{

	public class CsCustomTransientAttribute
	{

        [CustomTransient]
        String myTransient;

        String myPersistent;

        public void configure(){
            Db4o.configure().markTransient("com.db4o.test.cs.CustomTransient");
        }

        public void storeOne(){
            myTransient = "trans";
            myPersistent = "pers";
        }

        public void testOne(){
            Test.ensure(myTransient == null);
            Test.ensure(myPersistent.Equals("pers"));
        }
	}

    public class CustomTransient : Attribute {
        public CustomTransient() {
        }
    }

}
