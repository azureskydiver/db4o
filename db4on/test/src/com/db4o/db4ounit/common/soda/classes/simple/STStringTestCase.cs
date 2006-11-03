namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STStringTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
		, com.db4o.db4ounit.common.soda.STInterface
	{
		public string str;

		public STStringTestCase()
		{
		}

		public STStringTestCase(string str)
		{
			this.str = str;
		}

		/// <summary>needed for STInterface test</summary>
		public virtual object ReturnSomething()
		{
			return str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				(null), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa")
				, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb"), new 
				com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("dod") };
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
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase());
			q.Descend("str").Constrain("bbb");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("bbb"));
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("od"
				));
			q.Descend("str").Constraints().Contains();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("od"
				));
			q.Descend("str").Constraints().Contains().Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				(null), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa")
				, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb") });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("do"
				));
			q.Descend("str").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("od"
				));
			q.Descend("str").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[3]);
		}

		public virtual void TestStartsWith()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("do"
				));
			q.Descend("str").Constraints().StartsWith(true);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("od"
				));
			q.Descend("str").Constraints().StartsWith(true);
			Expect(q, new int[] {  });
		}

		public virtual void TestEndsWith()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("do"
				));
			q.Descend("str").Constraints().EndsWith(true);
			Expect(q, new int[] {  });
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("od"
				));
			q.Descend("str").Constraints().EndsWith(true);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("D"
				));
			q.Descend("str").Constraints().EndsWith(false);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				));
			q.Descend("str").Constraints().Like().Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				(null), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb")
				, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("dod") });
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("xxx"
				));
			q.Descend("str").Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase)set.Next();
			identityConstraint.str = "hihs";
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.str = "aaa";
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("aaa"));
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase)set.Next();
			identityConstraint.str = null;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.str = "aaa";
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				(null), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb")
				, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("dod") });
		}

		public virtual void TestNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase(null
				));
			q.Descend("str").Constrain(null);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				(null));
		}

		public virtual void TestNotNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase(null
				));
			q.Descend("str").Constrain(null).Not();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("aaa"), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb"
				), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("dod") });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				));
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("bbb"
				));
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}

		public virtual void TestEvaluation()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase(null
				));
			q.Constrain(new _AnonymousInnerClass179(this));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
		}

		private sealed class _AnonymousInnerClass179 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass179(STStringTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase sts = (com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
					)candidate.GetObject();
				candidate.Include(sts.str.IndexOf("od") == 1);
			}

			private readonly STStringTestCase _enclosing;
		}

		public virtual void TestCaseInsenstiveContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase)
				);
			q.Constrain(new _AnonymousInnerClass191(this));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				("dod"));
		}

		private sealed class _AnonymousInnerClass191 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass191(STStringTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase sts = (com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
					)candidate.GetObject();
				candidate.Include(sts.str.ToLower().IndexOf("od") >= 0);
			}

			private readonly STStringTestCase _enclosing;
		}
	}
}
