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

		public void StoreOne()
		{
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

		public void Test()
		{
			ExtObjectContainer oc = Tester.ObjectContainer();
			Query q = Tester.Query();
			q.Constrain(this.GetType());
			Query qd = q.Descend("simpleStruct");
			qd = qd.Descend("foo");
			qd.Constrain(100);

			ObjectSet objectSet = q.Execute();

			Tester.Ensure(objectSet.Size() == 1);
			CsStructs csStructs = (CsStructs)objectSet.Next();

			Tester.Ensure(csStructs.guid.ToString().Equals(GUID));
			Tester.Ensure(csStructs.simpleStruct.foo == 100);
			Tester.Ensure(csStructs.simpleStruct.bar.Equals("hi"));
			Tester.Ensure(csStructs.recursiveStruct.child.simpleStruct.foo == 22);
			Tester.Ensure(csStructs.recursiveStruct.child.simpleStruct.bar.Equals("jo"));
		}

	}

	public struct SimpleStruct
	{
		public int foo;
		public string bar;
	}

	public struct RecursiveStruct
	{
		public CsStructs child;
	}
}
