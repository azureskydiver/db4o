namespace com.db4o.db4ounit.common.header
{
	public class ConfigurationSettingsTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void TestChangingUuidSettings()
		{
			com.db4o.Db4o.Configure().GenerateUUIDs(0);
			Reopen();
			Db4oUnit.Assert.AreEqual(0, GenerateUUIDs());
			Db().Configure().GenerateUUIDs(-1);
			Db4oUnit.Assert.AreEqual(-1, GenerateUUIDs());
			Reopen();
			Db4oUnit.Assert.AreEqual(0, GenerateUUIDs());
		}

		private int GenerateUUIDs()
		{
			return ((com.db4o.YapFile)Db()).Config().GenerateUUIDs();
		}
	}
}
