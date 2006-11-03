namespace com.db4o.db4ounit.common.soda.joins.typed
{
	public class STOrTTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int orInt;

		public string orString;

		public STOrTTestCase()
		{
		}

		private STOrTTestCase(int a_int, string a_string)
		{
			orInt = a_int;
			orString = a_string;
		}

		public override string ToString()
		{
			return "STOr: int:" + orInt + " str:" + orString;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase
				(0, "hi"), new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(5, null), 
				new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(1000, "joho"), new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase
				(30000, "osoo"), new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(int.MaxValue
				 - 1, null) };
		}

		public virtual void TestSmallerGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			sub.Constrain(30000).Greater().Or(sub.Constrain(5).Smaller());
			Expect(q, new int[] { 0, 4 });
		}

		public virtual void TestGreaterGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			sub.Constrain(30000).Greater().Or(sub.Constrain(5).Greater());
			Expect(q, new int[] { 2, 3, 4 });
		}

		public virtual void TestGreaterEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			sub.Constrain(1000).Greater().Or(sub.Constrain(0));
			Expect(q, new int[] { 0, 3, 4 });
		}

		public virtual void TestEqualsNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(1000, null
				));
			q.Descend("orInt").Constraints().Or(q.Descend("orString").Constrain(null));
			Expect(q, new int[] { 1, 2, 4 });
		}

		public virtual void TestAndOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain(null))).Or(q
				.Descend("orInt").Constrain(1000).And(q.Descend("orString").Constrain("joho")));
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestOrAndOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(5).Or(q.Descend("orString").Constrain(null))).And(q
				.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho")));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[4]);
		}

		public virtual void TestOrOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Or(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain
				(null)));
			Expect(q, new int[] { 1, 2, 4 });
		}

		public virtual void TestMultiOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			((q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Or(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain
				("joho")))).Or((q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString"
				).Constrain(null))).And(q.Descend("orInt").Constrain(5).Or(q.Descend("orString")
				.Constrain(null))));
			Expect(q, new int[] { 1, 2, 4 });
		}

		public virtual void TestNotSmallerGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			(sub.Constrain(30000).Greater().Or(sub.Constrain(5).Smaller())).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestNotGreaterGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			(sub.Constrain(30000).Greater().Or(sub.Constrain(5).Greater())).Not();
			Expect(q, new int[] { 0, 1 });
		}

		public virtual void TestNotGreaterEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase());
			com.db4o.query.Query sub = q.Descend("orInt");
			(sub.Constrain(1000).Greater().Or(sub.Constrain(0))).Not();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestNotEqualsNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(1000, null
				));
			(q.Descend("orInt").Constraints().Or(q.Descend("orString").Constrain(null))).Not(
				);
			Expect(q, new int[] { 0, 3 });
		}

		public virtual void TestNotAndOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain(null))).Or(q
				.Descend("orInt").Constrain(1000).And(q.Descend("orString").Constrain("joho"))).
				Not();
			Expect(q, new int[] { 0, 3, 4 });
		}

		public virtual void TestNotOrAndOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(5).Or(q.Descend("orString").Constrain(null))).And(q
				.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Not();
			Expect(q, new int[] { 0, 1, 2, 3 });
		}

		public virtual void TestNotOrOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Or(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain
				(null))).Not();
			Expect(q, new int[] { 0, 3 });
		}

		public virtual void TestNotMultiOrAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			((q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Or(q.Descend("orInt").Constrain(5).And(q.Descend("orString").Constrain
				("joho")))).Or((q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString"
				).Constrain(null))).And(q.Descend("orInt").Constrain(5).Or(q.Descend("orString")
				.Constrain(null)))).Not();
			Expect(q, new int[] { 0, 3 });
		}

		public virtual void TestOrNotAndOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(int.MaxValue - 1).Or(q.Descend("orString").Constrain
				("joho"))).Not().And(q.Descend("orInt").Constrain(5).Or(q.Descend("orString").Constrain
				(null)));
			Expect(q, new int[] { 1 });
		}

		public virtual void TestAndNotAndAnd()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase(0, null));
			(q.Descend("orInt").Constrain(int.MaxValue - 1).And(q.Descend("orString").Constrain
				(null))).Not().And(q.Descend("orInt").Constrain(5).Or(q.Descend("orString").Constrain
				("osoo")));
			Expect(q, new int[] { 1, 3 });
		}
	}
}
