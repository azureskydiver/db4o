/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test.cs
{

	public class CsCustomTransientAttribute
	{

        [CustomTransient]
        String myTransient;

        String myPersistent;

        public void Configure(){
            Db4o.Configure().MarkTransient("com.db4o.test.cs.CustomTransient");
        }

        public void StoreOne(){
            myTransient = "trans";
            myPersistent = "pers";
        }

        public void TestOne(){
            Tester.Ensure(myTransient == null);
            Tester.Ensure(myPersistent.Equals("pers"));
        }
	}

    public class CustomTransient : Attribute {
        public CustomTransient() {
        }
    }

}
