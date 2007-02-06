namespace com.db4o.db4ounit.common.querying
{
	public class CascadedDeleteUpdate : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class ParentItem
		{
			public object child;
		}

		public class ChildItem
		{
			public object parent1;

			public object parent2;
		}

		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.querying.CascadedDeleteUpdate().RunClientServer();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem)
				).CascadeOnDelete(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem parentItem1 = new 
				com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem();
			com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem parentItem2 = new 
				com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem();
			com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ChildItem child = new com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ChildItem
				();
			child.parent1 = parentItem1;
			child.parent2 = parentItem2;
			parentItem1.child = child;
			parentItem2.child = child;
			Db().Set(parentItem1);
		}

		public virtual void TestAllObjectStored()
		{
			AssertAllObjectStored();
		}

		public virtual void TestUpdate()
		{
			com.db4o.query.Query q = NewQuery(typeof(com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem)
				);
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				Db().Set(objectSet.Next());
			}
			Db().Commit();
			AssertAllObjectStored();
		}

		private void AssertAllObjectStored()
		{
			Reopen();
			com.db4o.query.Query q = NewQuery(typeof(com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem)
				);
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem parentItem = (com.db4o.db4ounit.common.querying.CascadedDeleteUpdate.ParentItem
					)objectSet.Next();
				Db().Refresh(parentItem, 3);
				Db4oUnit.Assert.IsNotNull(parentItem.child);
			}
		}
	}
}
