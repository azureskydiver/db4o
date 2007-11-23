/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4objects.Db4o.Tests.Common.TA;
using Db4objects.Db4o.Tests.Common.TA.TA;

namespace Db4objects.Db4o.Tests.Common.TA.TA
{
	/// <exclude></exclude>
	public class TAArrayTestCase : TAItemTestCaseBase
	{
		private static readonly int[] INTS1 = new int[] { 1, 2, 3 };

		private static readonly int[] INTS2 = new int[] { 4, 5, 6 };

		private static readonly LinkedList[] LIST1 = new LinkedList[] { LinkedList.NewList
			(5), LinkedList.NewList(5) };

		private static readonly LinkedList[] LIST2 = new LinkedList[] { LinkedList.NewList
			(5), LinkedList.NewList(5) };

		public static void Main(string[] args)
		{
			new TAArrayTestCase().RunAll();
		}

		/// <exception cref="Exception"></exception>
		protected override object CreateItem()
		{
			TAArrayItem item = new TAArrayItem();
			item.value = INTS1;
			item.obj = INTS2;
			item.lists = LIST1;
			item.listsObject = LIST2;
			return item;
		}

		/// <exception cref="Exception"></exception>
		protected override void AssertItemValue(object obj)
		{
			TAArrayItem item = (TAArrayItem)obj;
			ArrayAssert.AreEqual(INTS1, item.Value());
			ArrayAssert.AreEqual(INTS2, (int[])item.Object());
			ArrayAssert.AreEqual(LIST1, item.Lists());
			ArrayAssert.AreEqual(LIST2, (LinkedList[])item.ListsObject());
		}
	}
}
