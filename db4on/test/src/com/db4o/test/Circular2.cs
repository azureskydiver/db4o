/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o;

namespace com.db4o.test
{

	public class Circular2
	{
        Hashtable ht;
    
        public void storeOne(){
            Test.objectContainer().configure().updateDepth(int.MaxValue);
            ht = new Hashtable();
            C2C c2c = new C2C();
            c2c.parent = this;
            ht["test"] = c2c;
        }
    
        public void testOne(){
            C2C c2c = (C2C)ht["test"];
            Test.ensure(c2c.parent == this);
            Test.objectContainer().configure().updateDepth(5);
        }
    
    }

    public class C2C{
        public Circular2 parent;
    }

}
