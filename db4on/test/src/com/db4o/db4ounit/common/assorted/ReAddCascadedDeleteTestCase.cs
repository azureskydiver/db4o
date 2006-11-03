namespace com.db4o.db4ounit.common.assorted
{
	public class ReAddCascadedDeleteTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase().RunSolo();
		}

		public class Item
		{
			public string _name;

			public com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item _member;

			public Item()
			{
			}

			public Item(string name)
			{
				_name = name;
			}

			public Item(string name, com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item
				 member)
			{
				_name = name;
				_member = member;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item)
				).CascadeOnDelete(true);
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item("parent"
				, new com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item("child"
				)));
		}

		public virtual void TestDeletingAndReaddingMember()
		{
			DeleteParentAndReAddChild();
			Reopen();
			Db4oUnit.Assert.IsNotNull(Query("child"));
			Db4oUnit.Assert.IsNull(Query("parent"));
		}

		private void DeleteParentAndReAddChild()
		{
			com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item i = Query("parent"
				);
			Db().Delete(i);
			Db().Set(i._member);
			Db().Commit();
		}

		private com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item Query(
			string name)
		{
			com.db4o.ObjectSet objectSet = Db().Get(new com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item
				(name));
			if (!objectSet.HasNext())
			{
				return null;
			}
			return (com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase.Item)objectSet
				.Next();
		}
	}
}
