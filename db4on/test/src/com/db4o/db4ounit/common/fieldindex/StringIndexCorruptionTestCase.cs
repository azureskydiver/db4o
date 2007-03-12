namespace com.db4o.db4ounit.common.fieldindex
{
	/// <summary>Jira ticket: COR-373</summary>
	/// <exclude></exclude>
	public class StringIndexCorruptionTestCase : com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.fieldindex.StringIndexCorruptionTestCase().RunSolo();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.BTreeNodeSize(4);
			config.FlushFileBuffers(false);
		}

		public virtual void TestStressSet()
		{
			com.db4o.ext.ExtObjectContainer container = Db();
			int itemCount = 300;
			for (int i = 0; i < itemCount; ++i)
			{
				com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item item = new com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item
					(ItemName(i));
				container.Set(item);
				container.Set(item);
				container.Commit();
				container.Set(item);
				container.Set(item);
				container.Commit();
			}
			for (int i = 0; i < itemCount; ++i)
			{
				string itemName = ItemName(i);
				com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item found = Query(itemName
					);
				Db4oUnit.Assert.IsNotNull(found, "'" + itemName + "' not found");
				Db4oUnit.Assert.AreEqual(itemName, found.name);
			}
		}

		private string ItemName(int i)
		{
			return "item " + i;
		}
	}
}
