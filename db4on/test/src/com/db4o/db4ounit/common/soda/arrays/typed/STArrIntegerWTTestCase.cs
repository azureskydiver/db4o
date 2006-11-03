namespace com.db4o.db4ounit.common.soda.arrays.typed
{
	public class STArrIntegerWTTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int[] intArr;

		public STArrIntegerWTTestCase()
		{
		}

		public STArrIntegerWTTestCase(int[] arr)
		{
			intArr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase
				(), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase(new int
				[0]), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase(new 
				int[] { 0, 0 }), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase
				(new int[] { 1, 17, int.MaxValue - 1 }), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase
				(new int[] { 3, 17, 25, int.MaxValue - 2 }) };
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase
				(new int[] { 17 }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase
				(new int[] { 17, 25 }));
			Expect(q, new int[] { 4 });
		}
	}
}
