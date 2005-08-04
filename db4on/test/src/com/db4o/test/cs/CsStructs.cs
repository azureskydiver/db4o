/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.ext;
using com.db4o.query;

namespace com.db4o.test.cs
{

    public class CsStructs
	{
        public static string GUID = "6a0d8033-444e-4b44-b0df-bf33dfe050f9";

        SimpleStruct simpleStruct;
        RecursiveStruct recursiveStruct;
        Guid guid;

		public CsStructs()
		{
		}

        public void storeOne(){
            simpleStruct.foo = 100;
            simpleStruct.bar = "hi";

            RecursiveStruct r = new RecursiveStruct();
            r.child = new CsStructs();

            SimpleStruct s = new SimpleStruct();
            s.foo = 22;
            s.bar = "jo";
            r.child.simpleStruct = s;

            recursiveStruct = r;

            guid = new Guid(GUID);
        }

        public void test(){
            ExtObjectContainer oc = Tester.objectContainer();
            Query q = Tester.query();
            q.constrain(this.GetType());
            Query qd = q.descend("simpleStruct");
            qd = qd.descend("foo");
            qd.constrain(100);
            ObjectSet objectSet = q.execute();

            Tester.ensure(objectSet.size() == 1);
            CsStructs csStructs = (CsStructs)objectSet.next();

            Tester.ensure(csStructs.guid.ToString().Equals(GUID));
            Tester.ensure(csStructs.simpleStruct.foo == 100);
            Tester.ensure(csStructs.simpleStruct.bar.Equals("hi"));
            Tester.ensure(csStructs.recursiveStruct.child.simpleStruct.foo == 22);
            Tester.ensure(csStructs.recursiveStruct.child.simpleStruct.bar.Equals("jo"));
        }

	}

    public struct SimpleStruct{
        public int foo;
        public string bar;
    }

    public struct RecursiveStruct{
        public CsStructs child;
    }
}
