namespace com.db4o.db4ounit.common.soda.ordered
{
	public class STOStringTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public string foo;

		public STOStringTestCase()
		{
		}

		public STOStringTestCase(string str)
		{
			this.foo = str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.ordered.STOStringTestCase
				(null), new com.db4o.db4ounit.common.soda.ordered.STOStringTestCase("bbb"), new 
				com.db4o.db4ounit.common.soda.ordered.STOStringTestCase("bbb"), new com.db4o.db4ounit.common.soda.ordered.STOStringTestCase
				("dod"), new com.db4o.db4ounit.common.soda.ordered.STOStringTestCase("aaa"), new 
				com.db4o.db4ounit.common.soda.ordered.STOStringTestCase("Xbb"), new com.db4o.db4ounit.common.soda.ordered.STOStringTestCase
				("bbq") };
		}

		public virtual void TestAscending()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOStringTestCase));
			q.Descend("foo").OrderAscending();
			ExpectOrdered(q, new int[] { 5, 4, 1, 2, 6, 3, 0 });
		}

		public virtual void TestDescending()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOStringTestCase));
			q.Descend("foo").OrderDescending();
			ExpectOrdered(q, new int[] { 3, 6, 2, 1, 4, 5, 0 });
		}

		public virtual void TestAscendingLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOStringTestCase));
			com.db4o.query.Query qStr = q.Descend("foo");
			qStr.Constrain("b").Like();
			qStr.OrderAscending();
			ExpectOrdered(q, new int[] { 5, 1, 2, 6 });
		}

		public virtual void TestDescendingContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOStringTestCase));
			com.db4o.query.Query qStr = q.Descend("foo");
			qStr.Constrain("b").Contains();
			qStr.OrderDescending();
			ExpectOrdered(q, new int[] { 6, 2, 1, 5 });
		}
	}
}
