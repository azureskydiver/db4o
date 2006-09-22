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
		public int? intValue = null;
		public DateTime? dateValue = null;

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
		static readonly DateTime TheDate = new DateTime(1983, 3, 7);
		
		public void Store()
		{
			Tester.Store(new NullableContainer(42));
			Tester.Store(new NullableContainer(TheDate));
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
					Tester.EnsureEquals(TheDate, item.dateValue.Value);
					Tester.Ensure(!item.intValue.HasValue);
					foundDate = true;
				}
			}

			Tester.Ensure(foundInt);
			Tester.Ensure(foundDate);
		}

		public void TestDateQuery()
		{
			ObjectSet os = Tester.ObjectContainer().Get(new NullableContainer(TheDate));
			CheckDateValueQueryResult(os);
		}

		private static void CheckDateValueQueryResult(ObjectSet os)
		{
			Tester.EnsureEquals(1, os.Size());
			NullableContainer found = (NullableContainer)os.Next();
			Tester.EnsureEquals(TheDate, found.dateValue.Value);
			EnsureIsNull(found.intValue);
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

		public void TestSodaQueryWithNullConstrain()
		{
			Query q = Tester.ObjectContainer().Query();
			q.Constrain(typeof(NullableContainer));
			q.Descend("intValue").Constrain(null);
			CheckDateValueQueryResult(q.Execute());
		}

	    private static void CheckIntValueQueryResult(ObjectSet os)
	    {
	        Tester.EnsureEquals(1, os.Size());
	        NullableContainer found = (NullableContainer)os.Next();
	        Tester.EnsureEquals(42, found.intValue.Value);
	    	EnsureIsNull(found.dateValue);
	    }

		private static void EnsureIsNull<T>(Nullable<T> value) where T : struct
		{
			Tester.Ensure("!nullable.HasValue", !value.HasValue);
		}
	}
#endif
}
