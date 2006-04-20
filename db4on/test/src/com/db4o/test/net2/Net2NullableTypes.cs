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
		public void store()
		{
			Tester.store(new NullableContainer(42));
			Tester.store(new NullableContainer(new DateTime(1983, 3, 7)));
		}

		public void testGlobalQuery()
		{
			Query query = Tester.query();
			query.constrain(typeof(NullableContainer));

			ObjectSet os = query.execute();
			Tester.ensureEquals(2, os.size());

			bool foundInt = false;
			bool foundDate = false;
			while (os.hasNext())
			{
				NullableContainer item = (NullableContainer)os.next();
				if (item.intValue.HasValue)
				{
					Tester.ensureEquals(42, item.intValue.Value);
					Tester.ensure(!item.dateValue.HasValue);
					foundInt = true;
				}
				else
				{
					Tester.ensureEquals(new DateTime(1983, 3, 7), item.dateValue.Value);
					Tester.ensure(!item.intValue.HasValue);
					foundDate = true;
				}
			}

			Tester.ensure(foundInt);
			Tester.ensure(foundDate);
		}

		public void testDateQuery()
		{
			DateTime value = new DateTime(1983, 3, 7);
			ObjectSet os = Tester.objectContainer().get(new NullableContainer(value));
			Tester.ensureEquals(1, os.size());

			NullableContainer found = (NullableContainer)os.next();
			Tester.ensureEquals(value, found.dateValue.Value);
			Tester.ensure(!found.intValue.HasValue);
		}

		public void testIntQuery()
		{	
			ObjectSet os = Tester.objectContainer().get(new NullableContainer(42));
		    checkIntValueQueryResult(os);
		}

        public void testSodaQuery()
        {
            Query q = Tester.objectContainer().query();
            q.constrain(typeof(NullableContainer));
            q.descend("intValue").constrain(42);
            checkIntValueQueryResult(q.execute());
        }

	    private static void checkIntValueQueryResult(ObjectSet os)
	    {
	        Tester.ensureEquals(1, os.size());
	        NullableContainer found = (NullableContainer)os.next();
	        Tester.ensureEquals(42, found.intValue.Value);
	        Tester.ensure(!found.dateValue.HasValue);
	    }

	}
#endif
}
