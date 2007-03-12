namespace com.db4o.db4ounit.common.assorted
{
	public class RollbackTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public string _string;

			public Item()
			{
			}

			public Item(string str)
			{
				_string = str;
			}
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.RollbackTestCase().RunClientServer();
		}

		public virtual void TestNotIsStoredOnRollback()
		{
			com.db4o.db4ounit.common.assorted.RollbackTestCase.Item item = new com.db4o.db4ounit.common.assorted.RollbackTestCase.Item
				();
			Store(item);
			Db().Rollback();
			Db4oUnit.Assert.IsFalse(Db().IsStored(item));
		}
	}
}
