namespace com.db4o.db4ounit.common.soda.arrays.untyped
{
	public class STArrMixedTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object[] arr;

		public STArrMixedTestCase()
		{
		}

		public STArrMixedTestCase(object[] arr)
		{
			this.arr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase
				(), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new object
				[0]), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new object
				[] { 0, 0, "foo", false }), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase
				(new object[] { 1, 17, int.MaxValue - 1, "foo", "bar" }), new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase
				(new object[] { 3, 17, 25, int.MaxValue - 2 }) };
		}

		public virtual void TestDefaultContainsInteger()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new 
				object[] { 17 }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsString()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new 
				object[] { "foo" }));
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestDefaultContainsBoolean()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new 
				object[] { false }));
			Expect(q, new int[] { 2 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase(new 
				object[] { 17, "bar" }));
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase)
				);
			q.Descend("arr").Constrain(17);
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("arr");
			qElements.Constrain(17);
			qElements.Constrain("bar");
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("arr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}
	}
}
