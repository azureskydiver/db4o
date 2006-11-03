namespace com.db4o.db4ounit.common.soda.experiments
{
	public class STMagicTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
		, com.db4o.db4ounit.common.soda.STInterface
	{
		public string str;

		public STMagicTestCase()
		{
		}

		private STMagicTestCase(string str)
		{
			this.str = str;
		}

		public override string ToString()
		{
			return "STMagicTestCase: " + str;
		}

		/// <summary>needed for STInterface test</summary>
		public virtual object ReturnSomething()
		{
			return str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.experiments.STMagicTestCase
				("aaa"), new com.db4o.db4ounit.common.soda.experiments.STMagicTestCase("aaax") };
		}

		/// <summary>
		/// Magic:
		/// Query for all objects with a known attribute,
		/// independant of the class or even if you don't
		/// know the class.
		/// </summary>
		/// <remarks>
		/// Magic:
		/// Query for all objects with a known attribute,
		/// independant of the class or even if you don't
		/// know the class.
		/// </remarks>
		public virtual void TestUnconstrainedClass()
		{
			com.db4o.query.Query q = NewQuery();
			q.Descend("str").Constrain("aaa");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.experiments.STMagicTestCase
				("aaa"), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase("aaa") });
		}

		/// <summary>
		/// Magic:
		/// Query for multiple classes.
		/// </summary>
		/// <remarks>
		/// Magic:
		/// Query for multiple classes.
		/// Every class gets it's own slot in the query graph.
		/// </remarks>
		public virtual void TestMultiClass()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase)
				).Or(q.Constrain(typeof(com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase)
				));
			object[] stDoubles = new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase
				().CreateData();
			object[] stStrings = new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase
				().CreateData();
			object[] res = new object[stDoubles.Length + stStrings.Length];
			System.Array.Copy(stDoubles, 0, res, 0, stDoubles.Length);
			System.Array.Copy(stStrings, 0, res, stDoubles.Length, stStrings.Length);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, res);
		}

		/// <summary>
		/// Magic:
		/// Execute any node in the query graph.
		/// </summary>
		/// <remarks>
		/// Magic:
		/// Execute any node in the query graph.
		/// The data for this example can be found in STTH1.java.
		/// </remarks>
		public virtual void TestExecuteAnyNode()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH1TestCase
				().CreateData()[5]);
			q = q.Descend("h2").Descend("h3");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH3
				("str3"));
		}

		/// <summary>
		/// Magic:
		/// Querying for an implemented Interface.
		/// </summary>
		/// <remarks>
		/// Magic:
		/// Querying for an implemented Interface.
		/// Using an Evaluation allows calls to the interface methods
		/// during the run of the query.s
		/// </remarks>
		public virtual void TestInterface()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.STInterface));
			q.Constrain(new _AnonymousInnerClass117(this));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, new object[] { new com.db4o.db4ounit.common.soda.experiments.STMagicTestCase
				("aaa"), new com.db4o.db4ounit.common.soda.classes.simple.STStringTestCase("aaa"
				) });
		}

		private sealed class _AnonymousInnerClass117 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass117(STMagicTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				com.db4o.db4ounit.common.soda.STInterface sti = (com.db4o.db4ounit.common.soda.STInterface
					)candidate.GetObject();
				candidate.Include(sti.ReturnSomething().Equals("aaa"));
			}

			private readonly STMagicTestCase _enclosing;
		}
	}
}
