/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

namespace com.db4o.test
{

	public class PrimitivesInCollection
	{

		IList list;

		public void StoreOne()
		{
			list = Tester.ObjectContainer().Collections().NewLinkedList();
			list.Add(1);
			list.Add("hi");
		}

		public void TestOne()
		{
			Tester.Ensure(list.Contains(1));
			Tester.Ensure(list.Contains("hi"));
		}
	}
}
