namespace com.db4o.db4ounit.common.soda.wrapper.untyped
{
	public class STStringUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object str;

		public STStringUTestCase()
		{
		}

		public STStringUTestCase(string str)
		{
			this.str = str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				(null), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb"), new 
				com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("dod") };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[2]);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[2]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[2]);
			q.Descend("str").Constraints().Not();
			Expect(q, new int[] { 0, 1, 3 });
		}

		public virtual void TestDescendantEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase()
				);
			q.Descend("str").Constrain("bbb");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				("bbb"));
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("od"
				));
			q.Descend("str").Constraints().Contains();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				("dod"));
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("od"
				));
			q.Descend("str").Constraints().Contains().Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				(null), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb") });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("do"
				));
			q.Descend("str").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				("dod"));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("od"
				));
			q.Descend("str").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[3]);
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				));
			q.Descend("str").Constraints().Like().Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				(null), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("dod") });
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("xxx"
				));
			q.Descend("str").Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase)set.Next();
			identityConstraint.str = "hihs";
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.str = "aaa";
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				("aaa"));
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase)set.Next();
			identityConstraint.str = null;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.str = "aaa";
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				(null), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("dod") });
		}

		public virtual void TestNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase(null
				));
			q.Descend("str").Constrain(null);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				(null));
		}

		public virtual void TestNotNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase(null
				));
			q.Descend("str").Constrain(null).Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase
				("aaa"), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("dod") });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa"
				));
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("bbb"
				));
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}
	}
}
