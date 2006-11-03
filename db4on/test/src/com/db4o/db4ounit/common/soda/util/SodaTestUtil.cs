namespace com.db4o.db4ounit.common.soda.util
{
	public class SodaTestUtil
	{
		public static void ExpectOne(com.db4o.query.Query query, object @object)
		{
			Expect(query, new object[] { @object });
		}

		public static void ExpectNone(com.db4o.query.Query query)
		{
			Expect(query, null);
		}

		public static void Expect(com.db4o.query.Query query, object[] results)
		{
			Expect(query, results, false);
		}

		public static void ExpectOrdered(com.db4o.query.Query query, object[] results)
		{
			Expect(query, results, true);
		}

		public static void Expect(com.db4o.query.Query query, object[] results, bool ordered
			)
		{
			com.db4o.ObjectSet set = query.Execute();
			if (results == null || results.Length == 0)
			{
				if (set.Size() > 0)
				{
					Db4oUnit.Assert.Fail("No content expected.");
				}
				return;
			}
			int j = 0;
			if (set.Size() == results.Length)
			{
				while (set.HasNext())
				{
					object obj = set.Next();
					bool found = false;
					if (ordered)
					{
						if (com.db4o.db4ounit.common.soda.util.TCompare.IsEqual(results[j], obj))
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
								if (com.db4o.db4ounit.common.soda.util.TCompare.IsEqual(results[i], obj))
								{
									results[i] = null;
									found = true;
									break;
								}
							}
						}
					}
					if (!found)
					{
						Db4oUnit.Assert.Fail("Object not expected: " + obj);
					}
				}
				for (int i = 0; i < results.Length; i++)
				{
					if (results[i] != null)
					{
						Db4oUnit.Assert.Fail("Expected object not returned: " + results[i]);
					}
				}
			}
			else
			{
				Db4oUnit.Assert.Fail("Unexpected size returned.\nExpected: " + results.Length + " Returned: "
					 + set.Size());
			}
		}

		private SodaTestUtil()
		{
		}
	}
}
