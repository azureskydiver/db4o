#if NET_2_0
using System;
using System.Collections.Generic;
using System.Text;
using com.db4o;
using com.db4o.test;
using com.db4o.query;

namespace com.db4o.test.net2
{
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
			Test.store(new NullableContainer(42));
			Test.store(new NullableContainer(new DateTime(1983, 3, 7)));
		}

		public void testGlobalQuery()
		{
			Query query = Test.query();
			query.constrain(typeof(NullableContainer));

			ObjectSet os = query.execute();
			Test.ensureEquals(2, os.size());

			bool foundInt = false;
			bool foundDate = false;
			while (os.hasNext())
			{
				NullableContainer item = (NullableContainer)os.next();
				if (item.intValue.HasValue)
				{
					Test.ensureEquals(42, item.intValue.Value);
					Test.ensure(!item.dateValue.HasValue);
					foundInt = true;
				}
				else
				{
					Test.ensureEquals(new DateTime(1983, 3, 7), item.dateValue.Value);
					Test.ensure(!item.intValue.HasValue);
					foundDate = true;
				}
			}

			Test.ensure(foundInt);
			Test.ensure(foundDate);
		}

		public void testDateQuery()
		{
			DateTime value = new DateTime(1983, 3, 7);
			ObjectSet os = Test.objectContainer().get(new NullableContainer(value));
			Test.ensureEquals(1, os.size());

			NullableContainer found = (NullableContainer)os.next();
			Test.ensureEquals(value, found.dateValue.Value);
			Test.ensure(!found.intValue.HasValue);
		}

		public void testIntQuery()
		{	
			ObjectSet os = Test.objectContainer().get(new NullableContainer(42));
			Test.ensureEquals(1, os.size());

			NullableContainer found = (NullableContainer)os.next();
			Test.ensureEquals(42, found.intValue.Value);
			Test.ensure(!found.dateValue.HasValue);
		}

	}
}
#endif