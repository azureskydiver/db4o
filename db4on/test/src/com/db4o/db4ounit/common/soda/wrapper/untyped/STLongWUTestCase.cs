namespace com.db4o.db4ounit.common.soda.wrapper.untyped
{
	public class STLongWUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object i_long;

		public STLongWUTestCase()
		{
		}

		private STLongWUTestCase(long a_long)
		{
			i_long = a_long;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase
				(long.MinValue), new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase
				(-1), new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase(0), new 
				com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase(long.MaxValue - 1
				) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase(long.MinValue
				));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase
				(long.MinValue) });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase(-1
				));
			q.Descend("i_long").Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase(1)
				);
			q.Descend("i_long").Constraints().Smaller();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestBetween()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase());
			com.db4o.query.Query sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(-3)).Greater();
			sub.Constrain(System.Convert.ToInt64(3)).Smaller();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase());
			com.db4o.query.Query sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(-3)).Greater().And(sub.Constrain(System.Convert.ToInt64
				(3)).Smaller());
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase());
			com.db4o.query.Query sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(3)).Greater().Or(sub.Constrain(System.Convert.ToInt64
				(-3)).Smaller());
			Expect(q, new int[] { 0, 3 });
		}
	}
}
