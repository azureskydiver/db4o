namespace Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped
{
	public class STLongWUTestCase : Db4objects.Db4o.Tests.Common.Soda.Util.SodaBaseTestCase
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
			return new object[] { new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				(long.MinValue), new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				(-1), new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase(0), 
				new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase(long.MaxValue
				 - 1) };
		}

		public virtual void TestEquals()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				(long.MinValue));
			Db4objects.Db4o.Tests.Common.Soda.Util.SodaTestUtil.Expect(q, new object[] { new 
				Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase(long.MinValue
				) });
		}

		public virtual void TestGreater()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				(-1));
			q.Descend("i_long").Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				(1));
			q.Descend("i_long").Constraints().Smaller();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestBetween()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				());
			Db4objects.Db4o.Query.IQuery sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(-3)).Greater();
			sub.Constrain(System.Convert.ToInt64(3)).Smaller();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestAnd()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				());
			Db4objects.Db4o.Query.IQuery sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(-3)).Greater().And(sub.Constrain(System.Convert.ToInt64
				(3)).Smaller());
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestOr()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Wrapper.Untyped.STLongWUTestCase
				());
			Db4objects.Db4o.Query.IQuery sub = q.Descend("i_long");
			sub.Constrain(System.Convert.ToInt64(3)).Greater().Or(sub.Constrain(System.Convert.ToInt64
				(-3)).Smaller());
			Expect(q, new int[] { 0, 3 });
		}
	}
}
