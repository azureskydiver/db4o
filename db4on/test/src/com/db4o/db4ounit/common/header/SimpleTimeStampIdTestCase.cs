namespace com.db4o.db4ounit.common.header
{
	public class SimpleTimeStampIdTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase().RunSolo();
		}

		public class STSItem
		{
			public string _name;

			public STSItem()
			{
			}

			public STSItem(string name)
			{
				_name = name;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			com.db4o.config.ObjectClass objectClass = config.ObjectClass(typeof(com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem)
				);
			objectClass.GenerateUUIDs(true);
			objectClass.GenerateVersionNumbers(true);
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem("one"
				));
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem item = (com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem
				)Db().Get(typeof(com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem)
				).Next();
			long version = Db().GetObjectInfo(item).GetVersion();
			Db4oUnit.Assert.IsGreater(0, version);
			Db4oUnit.Assert.IsGreaterOrEqual(version, CurrentVersion());
			Reopen();
			com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem item2 = new com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem
				("two");
			Db().Set(item2);
			long secondVersion = Db().GetObjectInfo(item2).GetVersion();
			Db4oUnit.Assert.IsGreater(version, secondVersion);
			Db4oUnit.Assert.IsGreaterOrEqual(secondVersion, CurrentVersion());
		}

		private long CurrentVersion()
		{
			return ((com.db4o.YapFile)Db()).CurrentVersion();
		}
	}
}
