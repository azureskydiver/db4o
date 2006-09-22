namespace com.db4o.db4ounit.common.header
{
	public class SimpleTimeStampIdTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
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

		protected override void Configure()
		{
			com.db4o.config.ObjectClass objectClass = com.db4o.Db4o.Configure().ObjectClass(typeof(
				com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem));
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
				)Db().Get(typeof(com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem
				)).Next();
			long version = Db().GetObjectInfo(item).GetVersion();
			Db4oUnit.Assert.IsTrue(version > 0);
			Db4oUnit.Assert.IsTrue(((com.db4o.YapFile)Db()).CurrentVersion() >= version);
			Reopen();
			com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem item2 = new com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase.STSItem
				("two");
			Db().Set(item2);
			long secondVersion = Db().GetObjectInfo(item2).GetVersion();
			Db4oUnit.Assert.IsTrue(secondVersion > version);
			Db4oUnit.Assert.IsTrue(((com.db4o.YapFile)Db()).CurrentVersion() >= version);
		}
	}
}
