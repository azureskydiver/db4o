namespace com.db4o.db4ounit.common.querying
{
	public class ConjunctiveQbETestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Sup
		{
			public bool _flag;

			public Sup(bool flag)
			{
				this._flag = flag;
			}

			public virtual com.db4o.ObjectSet Query(com.db4o.ObjectContainer db)
			{
				com.db4o.query.Query query = db.Query();
				query.Constrain(this);
				query.Descend("_flag").Constrain(true).Not();
				return query.Execute();
			}
		}

		public class Sub1 : com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sup
		{
			public Sub1(bool flag) : base(flag)
			{
			}
		}

		public class Sub2 : com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sup
		{
			public Sub2(bool flag) : base(flag)
			{
			}
		}

		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sub1(false));
			Store(new com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sub1(true));
			Store(new com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sub2(false));
			Store(new com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sub2(true));
		}

		public virtual void TestAndedQbE()
		{
			Db4oUnit.Assert.AreEqual(1, new com.db4o.db4ounit.common.querying.ConjunctiveQbETestCase.Sub1
				(false).Query(Db()).Size());
		}
	}
}
