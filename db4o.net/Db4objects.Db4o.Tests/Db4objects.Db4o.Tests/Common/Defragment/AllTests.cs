/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Tests.Common.Defragment;

namespace Db4objects.Db4o.Tests.Common.Defragment
{
	public class AllTests : Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new Db4objects.Db4o.Tests.Common.Defragment.AllTests().RunSolo();
		}

		protected override Type[] TestCases()
		{
			return new Type[] { typeof(COR775TestCase), typeof(BlockSizeDefragTestCase), typeof(
				DefragInheritedFieldIndexTestCase), typeof(SlotDefragmentTestCase), typeof(StoredClassFilterTestCase
				), typeof(TranslatedDefragTestCase) };
		}
	}
}
