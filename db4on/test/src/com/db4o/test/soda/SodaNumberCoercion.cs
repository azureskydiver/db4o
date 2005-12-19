using System;
using System.Collections.Generic;
using System.Text;
using com.db4o.query;

namespace com.db4o.test.soda
{
	class SodaNumberCoercion
	{
		class Thing
		{
			public long value;
			
			public Thing(long value)
			{
				this.value = value;
			}
		}
		
		public void store()
		{
			Tester.store(new Thing(10));
			Tester.store(new Thing(100));
			Tester.store(new Thing(42));
		}
		
		public void testIntQueryOnLongField()
		{
			Query q = Tester.query();
			q.constrain(typeof (Thing));
			q.descend("value").constrain(100);
			Tester.ensureEquals(1, q.execute().Count, "testIntQueryOnLongField");
		}
		
	}
}
