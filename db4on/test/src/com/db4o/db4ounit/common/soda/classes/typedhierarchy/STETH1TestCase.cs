namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	/// <summary>ETH: Extends Typed Hierarchy</summary>
	public class STETH1TestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public string foo1;

		public STETH1TestCase()
		{
		}

		public STETH1TestCase(string str)
		{
			foo1 = str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase
				(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase("str1"
				), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH2(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH2
				("str1", "str2"), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH3
				(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH3("str1a", "str2"
				, "str3"), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH3("str1a"
				, "str2a", null), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4
				(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4("str1a", "str2"
				, "str4"), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4("str1b"
				, "str2a", "str4") };
		}

		public virtual void TestStrNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			Expect(q, new int[] { 0, 2, 4, 7 });
		}

		public virtual void TestTwoNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			q.Descend("foo3").Constrain(null);
			Expect(q, new int[] { 0, 2, 4, 7 });
		}

		public virtual void TestClass()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH2));
			Expect(q, new int[] { 2, 3, 4, 5, 6, 7, 8, 9 });
		}

		public virtual void TestOrClass()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH3)).
				Or(q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4)
				));
			Expect(q, new int[] { 4, 5, 6, 7, 8, 9 });
		}

		public virtual void TestAndClass()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase)
				);
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4));
			Expect(q, new int[] { 7, 8, 9 });
		}

		public virtual void TestParalellDescendantPaths()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH3)).
				Or(q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH4)
				));
			q.Descend("foo3").Constrain("str3").Or(q.Descend("foo4").Constrain("str4"));
			Expect(q, new int[] { 5, 8, 9 });
		}

		public virtual void TestOrObjects()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[3]).Or(q.Constrain(_array[5]));
			Expect(q, new int[] { 3, 5 });
		}
	}
}
