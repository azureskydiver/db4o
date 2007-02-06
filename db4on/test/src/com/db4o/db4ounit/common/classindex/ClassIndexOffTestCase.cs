namespace com.db4o.db4ounit.common.classindex
{
	public class ClassIndexOffTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public class Item
		{
			public string name;

			public Item(string _name)
			{
				this.name = _name;
			}
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.classindex.ClassIndexOffTestCase().RunSolo();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.classindex.ClassIndexOffTestCase.Item)
				).Indexed(false);
		}

		public virtual void Test()
		{
			Db().Set(new com.db4o.db4ounit.common.classindex.ClassIndexOffTestCase.Item("1"));
			com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)Db().StoredClass
				(typeof(com.db4o.db4ounit.common.classindex.ClassIndexOffTestCase.Item));
			Db4oUnit.Assert.IsFalse(yc.HasIndex());
			AssertNoItemFound();
			Db().Commit();
			AssertNoItemFound();
		}

		private void AssertNoItemFound()
		{
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.classindex.ClassIndexOffTestCase.Item)
				);
			Db4oUnit.Assert.AreEqual(0, q.Execute().Size());
		}
	}
}
