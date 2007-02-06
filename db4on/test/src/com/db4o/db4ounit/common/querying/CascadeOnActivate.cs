namespace com.db4o.db4ounit.common.querying
{
	public class CascadeOnActivate : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public string name;

		public com.db4o.db4ounit.common.querying.CascadeOnActivate child;

		protected override void Configure(com.db4o.config.Configuration conf)
		{
			conf.ObjectClass(this).CascadeOnActivate(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadeOnActivate coa = new com.db4o.db4ounit.common.querying.CascadeOnActivate
				();
			coa.name = "1";
			coa.child = new com.db4o.db4ounit.common.querying.CascadeOnActivate();
			coa.child.name = "2";
			coa.child.child = new com.db4o.db4ounit.common.querying.CascadeOnActivate();
			coa.child.child.name = "3";
			Db().Set(coa);
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery(GetType());
			q.Descend("name").Constrain("1");
			com.db4o.ObjectSet os = q.Execute();
			com.db4o.db4ounit.common.querying.CascadeOnActivate coa = (com.db4o.db4ounit.common.querying.CascadeOnActivate
				)os.Next();
			com.db4o.db4ounit.common.querying.CascadeOnActivate coa3 = coa.child.child;
			Db4oUnit.Assert.AreEqual("3", coa3.name);
			Db().Deactivate(coa, int.MaxValue);
			Db4oUnit.Assert.IsNull(coa3.name);
			Db().Activate(coa, 1);
			Db4oUnit.Assert.AreEqual("3", coa3.name);
		}
	}
}
