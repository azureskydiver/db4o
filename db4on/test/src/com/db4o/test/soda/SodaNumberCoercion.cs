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
		
		public void Store()
		{
			Tester.Store(new Thing(10));
			Tester.Store(new Thing(100));
			Tester.Store(new Thing(42));
		}
		
		public void TestIntQueryOnLongField()
		{
			Query q = Tester.Query();
			q.Constrain(typeof (Thing));
			q.Descend("value").Constrain(100);
			Tester.EnsureEquals(1, q.Execute().Count, "testIntQueryOnLongField");
		}
		
	}
}
