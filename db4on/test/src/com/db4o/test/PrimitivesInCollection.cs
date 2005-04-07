/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

namespace com.db4o.test
{

	public class PrimitivesInCollection
	{

		IList list;

		public void storeOne()
		{
			list = Test.objectContainer().collections().newLinkedList();
			list.Add(1);
			list.Add("hi");
		}

		public void testOne()
		{
			Test.ensure(list.Contains(1));
			Test.ensure(list.Contains("hi"));
		}
	}
}
