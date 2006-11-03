namespace com.db4o.db4ounit.common.soda.arrays.untyped
{
	public class STArrStringUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object[] strArr;

		public STArrStringUTestCase()
		{
		}

		public STArrStringUTestCase(object[] arr)
		{
			strArr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase(new object
				[] { null }), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(new object[] { null, null }), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(new object[] { "foo", "bar", "fly" }), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(new object[] { null, "bar", "wohay", "johy" }) };
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(new object[] { "bar" }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase
				(new object[] { "foo", "bar" }));
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase)
				);
			q.Descend("strArr").Constrain("bar");
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("strArr");
			qElements.Constrain("foo");
			qElements.Constrain("bar");
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOneNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase)
				);
			q.Descend("strArr").Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestDescendTwoNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("strArr");
			qElements.Constrain("foo").Not();
			qElements.Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}
	}
}
