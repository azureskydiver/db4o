namespace com.db4o.db4ounit.common.soda.classes.typedhierarchy
{
	/// <summary>SDFT: Same descendant field typed</summary>
	public class STSDFT1TestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public STSDFT1TestCase()
		{
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase
				(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT2(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT2
				("str1"), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT2("str2"
				), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT3(), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT3
				("str1"), new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT3("str3"
				) };
		}

		public virtual void TestStrNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase
				());
			q.Descend("foo").Constrain(null);
			Expect(q, new int[] { 0, 1, 4 });
		}

		public virtual void TestStrVal()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase)
				);
			q.Descend("foo").Constrain("str1");
			Expect(q, new int[] { 2, 5 });
		}

		public virtual void TestOrValue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase)
				);
			com.db4o.query.Query foo = q.Descend("foo");
			foo.Constrain("str1").Or(foo.Constrain("str2"));
			Expect(q, new int[] { 2, 3, 5 });
		}

		public virtual void TestOrNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase)
				);
			com.db4o.query.Query foo = q.Descend("foo");
			foo.Constrain("str1").Or(foo.Constrain(null));
			Expect(q, new int[] { 0, 1, 2, 4, 5 });
		}

		public virtual void TestTripleOrNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase)
				);
			com.db4o.query.Query foo = q.Descend("foo");
			foo.Constrain("str1").Or(foo.Constrain(null)).Or(foo.Constrain("str2"));
			Expect(q, new int[] { 0, 1, 2, 3, 4, 5 });
		}
	}
}
