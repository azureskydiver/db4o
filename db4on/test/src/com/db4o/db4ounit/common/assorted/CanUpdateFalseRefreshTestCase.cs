namespace com.db4o.db4ounit.common.assorted
{
	public class CanUpdateFalseRefreshTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public int _id;

			public string _name;

			public Item(int id, string name)
			{
				_id = id;
				_name = name;
			}

			public virtual bool ObjectCanUpdate(com.db4o.ObjectContainer container)
			{
				return false;
			}
		}

		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase.Item(1, 
				"one"));
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase.Item item = (com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase.Item
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase.Item)
				);
			item._name = "two";
			Db().Set(item);
			Db4oUnit.Assert.AreEqual("two", item._name);
			Db().Refresh(item, 2);
			Db4oUnit.Assert.AreEqual("one", item._name);
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase().RunSoloAndClientServer
				();
		}
	}
}
