/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using com.db4o.query;

namespace com.db4o.test.cs
{
	
	public class CsCascadeDeleteToStructs
	{
		CDSStruct myStruct;

		public void storeOne()
		{
			myStruct = new CDSStruct(3,"hi");
		}

		public void testOne()
		{
			Test.ensureOccurrences(myStruct,1);
			myStruct.foo = 44;
			myStruct.bar = "cool";
			Test.objectContainer().set(this);
			Test.ensureOccurrences(myStruct,1);

			Test.objectContainer().delete(this);
			Test.commit();
			Test.ensureOccurrences(myStruct,0);
		}

	}

	public struct CDSStruct
	{
		public int foo;
		public string bar;


		public CDSStruct(int foo, string bar)
		{
			this.foo = foo;
			this.bar = bar;
		}
	}
}
