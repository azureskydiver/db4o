namespace com.db4o.db4ounit.common.assorted
{
	public class CascadedDeleteReaddTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item _child1;

			public com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item _child2;

			public string _name;

			public Item()
			{
			}

			public Item(string name)
			{
				_name = name;
			}

			public Item(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item child1
				, com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item child2, string
				 name)
			{
				_child1 = child1;
				_child2 = child2;
				_name = name;
			}
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase().RunSoloAndClientServer
				();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				).ObjectField("_child1").CascadeOnDelete(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				).ObjectField("_child2").CascadeOnDelete(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				).ObjectField("_child1").CascadeOnUpdate(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				).ObjectField("_child2").CascadeOnUpdate(true);
		}

		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item(new 
				com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item("1"), null, "parent"
				));
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item item = ParentItem
				();
			item._child2 = item._child1;
			item._child1 = null;
			Store(item);
			Db().Delete(item);
			AssertItemCount(0);
		}

		private com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item ParentItem
			()
		{
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				);
			q.Descend("_name").Constrain("parent");
			return (com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)q.Execute
				().Next();
		}

		private void AssertItemCount(int count)
		{
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase.Item)
				);
			com.db4o.ObjectSet objectSet = q.Execute();
			Db4oUnit.Assert.AreEqual(count, objectSet.Size());
		}
	}
}
