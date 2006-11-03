namespace com.db4o.db4ounit.common.soda.classes.untypedhierarchy
{
	/// <summary>RUH: RoundTrip Untyped Hierarchy</summary>
	public class STRUH1TestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object h2;

		public string foo1;

		public STRUH1TestCase()
		{
		}

		public STRUH1TestCase(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2
			 a2)
		{
			h2 = a2;
		}

		public STRUH1TestCase(string str)
		{
			foo1 = str;
		}

		public STRUH1TestCase(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2
			 a2, string str)
		{
			h2 = a2;
			foo1 = str;
		}

		public override object[] CreateData()
		{
			com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase[] objects = 
				{ new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase(), new 
				com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase("str1"), new 
				com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2
				()), new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase(new 
				com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2("str2")), new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3
				("str3"))), new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3
				("str3"), "str2")) };
			for (int i = 0; i < objects.Length; i++)
			{
				objects[i].AdjustParents();
			}
			return objects;
		}

		/// <summary>this is the special part of this test: circular references</summary>
		internal virtual void AdjustParents()
		{
			if (h2 != null)
			{
				com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2 th2 = (com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2
					)h2;
				th2.parent = this;
				if (th2.h3 != null)
				{
					com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3 th3 = (com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3
						)th2.h3;
					th3.parent = th2;
					th3.grandParent = this;
				}
			}
		}

		public virtual void TestStrNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			Expect(q, new int[] { 0, 2, 3, 4, 5 });
		}

		public virtual void TestBothNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			q.Descend("h2").Constrain(null);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestDescendantNotNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			q.Descend("h2").Constrain(null).Not();
			Expect(q, new int[] { 2, 3, 4, 5 });
		}

		public virtual void TestDescendantDescendantNotNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			q.Descend("h2").Descend("h3").Constrain(null).Not();
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantExists()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[2]);
			Expect(q, new int[] { 2, 3, 4, 5 });
		}

		public virtual void TestDescendantValue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[3]);
			Expect(q, new int[] { 3, 5 });
		}

		public virtual void TestDescendantDescendantExists()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3
				())));
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantDescendantValue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3
				("str3"))));
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantDescendantStringPath()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			q.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3");
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestSequentialAddition()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				());
			com.db4o.query.Query cur = q.Descend("h2");
			cur.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH2()
				);
			cur.Descend("foo2").Constrain("str2");
			cur = cur.Descend("h3");
			cur.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH3()
				);
			cur.Descend("foo3").Constrain("str3");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[5]);
		}

		public virtual void TestTwoLevelOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				("str1"));
			q.Descend("foo1").Constraints().Or(q.Descend("h2").Descend("h3").Descend("foo3").
				Constrain("str3"));
			Expect(q, new int[] { 1, 4, 5 });
		}

		public virtual void TestThreeLevelOr()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase
				("str1"));
			q.Descend("foo1").Constraints().Or(q.Descend("h2").Descend("foo2").Constrain("str2"
				)).Or(q.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3"));
			Expect(q, new int[] { 1, 3, 4, 5 });
		}
	}
}
