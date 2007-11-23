/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Tests.Common.Soda.Util;

namespace Db4objects.Db4o.Tests.Common.Soda.Util
{
	public class SodaTestUtil
	{
		public static void ExpectOne(IQuery query, object @object)
		{
			Expect(query, new object[] { @object });
		}

		public static void ExpectNone(IQuery query)
		{
			Expect(query, null);
		}

		public static void Expect(IQuery query, object[] results)
		{
			Expect(query, results, false);
		}

		public static void ExpectOrdered(IQuery query, object[] results)
		{
			Expect(query, results, true);
		}

		public static void Expect(IQuery query, object[] results, bool ordered)
		{
			IObjectSet set = query.Execute();
			if (results == null || results.Length == 0)
			{
				if (set.Size() > 0)
				{
					Assert.Fail("No content expected.");
				}
				return;
			}
			int j = 0;
			Assert.AreEqual(set.Size(), results.Length);
			while (set.HasNext())
			{
				object obj = set.Next();
				bool found = false;
				if (ordered)
				{
					if (TCompare.IsEqual(results[j], obj))
					{
						results[j] = null;
						found = true;
					}
					j++;
				}
				else
				{
					for (int i = 0; i < results.Length; i++)
					{
						if (results[i] != null)
						{
							if (TCompare.IsEqual(results[i], obj))
							{
								results[i] = null;
								found = true;
								break;
							}
						}
					}
				}
				Assert.IsTrue(found, "Object not expected: " + obj);
			}
			for (int i = 0; i < results.Length; i++)
			{
				if (results[i] != null)
				{
					Assert.Fail("Expected object not returned: " + results[i]);
				}
			}
		}

		private SodaTestUtil()
		{
		}
	}
}
