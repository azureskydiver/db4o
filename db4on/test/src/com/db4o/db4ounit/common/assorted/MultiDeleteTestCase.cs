namespace com.db4o.db4ounit.common.assorted
{
	public class MultiDeleteTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutDefragSolo
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.MultiDeleteTestCase().RunSoloAndClientServer
				();
		}

		public class Item
		{
			public com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item child;

			public string name;

			public object forLong;

			public long myLong;

			public object[] untypedArr;

			public long[] typedArr;

			public virtual void SetMembers()
			{
				forLong = System.Convert.ToInt64(100);
				myLong = System.Convert.ToInt64(100);
				untypedArr = new object[] { System.Convert.ToInt64(10), "hi", new com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item
					() };
				typedArr = new long[] { System.Convert.ToInt64(3), System.Convert.ToInt64(7), System.Convert.ToInt64
					(9) };
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			com.db4o.config.ObjectClass itemClass = config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item)
				);
			itemClass.CascadeOnDelete(true);
			itemClass.CascadeOnUpdate(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item md = new com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item
				();
			md.name = "killmefirst";
			md.SetMembers();
			md.child = new com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item();
			md.child.SetMembers();
			Db().Set(md);
		}

		public virtual void TestDeleteCanBeCalledTwice()
		{
			com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item item = ItemByName("killmefirst"
				);
			Db4oUnit.Assert.IsNotNull(item);
			long id = Db().GetID(item);
			Db().Delete(item);
			Db4oUnit.Assert.AreSame(item, ItemById(id));
			Db().Delete(item);
			Db4oUnit.Assert.AreSame(item, ItemById(id));
		}

		private com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item ItemByName(string
			 name)
		{
			com.db4o.query.Query q = NewQuery(typeof(com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item)
				);
			q.Descend("name").Constrain(name);
			return (com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item)q.Execute().Next
				();
		}

		private com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item ItemById(long 
			id)
		{
			return (com.db4o.db4ounit.common.assorted.MultiDeleteTestCase.Item)Db().GetByID(id
				);
		}
	}
}
