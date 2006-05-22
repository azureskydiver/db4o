namespace com.db4o.test.net2
{
#if NET_2_0 || CF_2_0
    using System;
	using System.Collections.Generic;
	using System.Text;
	using com.db4o;
	using com.db4o.test;
	using com.db4o.query;

	class NullableContainer
	{
		public int? intValue;
		public DateTime? dateValue;

		public NullableContainer(int value)
		{
			this.intValue = value;
		}

		public NullableContainer(DateTime value)
		{
			this.dateValue = value;
		}
	}

	class Net2NullableTypes
	{
		public void Store()
		{
			Tester.Store(new NullableContainer(42));
			Tester.Store(new NullableContainer(new DateTime(1983, 3, 7)));
		}

		public void TestGlobalQuery()
		{
			Query query = Tester.Query();
			query.Constrain(typeof(NullableContainer));

			ObjectSet os = query.Execute();
			Tester.EnsureEquals(2, os.Size());

			bool foundInt = false;
			bool foundDate = false;
			while (os.HasNext())
			{
				NullableContainer item = (NullableContainer)os.Next();
				if (item.intValue.HasValue)
				{
					Tester.EnsureEquals(42, item.intValue.Value);
					Tester.Ensure(!item.dateValue.HasValue);
					foundInt = true;
				}
				else
				{
					Tester.EnsureEquals(new DateTime(1983, 3, 7), item.dateValue.Value);
					Tester.Ensure(!item.intValue.HasValue);
					foundDate = true;
				}
			}

			Tester.Ensure(foundInt);
			Tester.Ensure(foundDate);
		}

		public void TestDateQuery()
		{
			DateTime value = new DateTime(1983, 3, 7);
			ObjectSet os = Tester.ObjectContainer().Get(new NullableContainer(value));
			Tester.EnsureEquals(1, os.Size());

			NullableContainer found = (NullableContainer)os.Next();
			Tester.EnsureEquals(value, found.dateValue.Value);
			Tester.Ensure(!found.intValue.HasValue);
		}

		public void TestIntQuery()
		{	
			ObjectSet os = Tester.ObjectContainer().Get(new NullableContainer(42));
		    CheckIntValueQueryResult(os);
		}

        public void TestSodaQuery()
        {
            Query q = Tester.ObjectContainer().Query();
            q.Constrain(typeof(NullableContainer));
            q.Descend("intValue").Constrain(42);
            CheckIntValueQueryResult(q.Execute());
        }

	    private static void CheckIntValueQueryResult(ObjectSet os)
	    {
	        Tester.EnsureEquals(1, os.Size());
	        NullableContainer found = (NullableContainer)os.Next();
	        Tester.EnsureEquals(42, found.intValue.Value);
	        Tester.Ensure(!found.dateValue.HasValue);
	    }

	}
#endif
}
