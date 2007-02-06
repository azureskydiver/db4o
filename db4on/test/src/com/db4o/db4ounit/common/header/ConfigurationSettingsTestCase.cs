namespace com.db4o.db4ounit.common.header
{
	public class ConfigurationSettingsTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public virtual void TestChangingUuidSettings()
		{
			Fixture().Config().GenerateUUIDs(0);
			Reopen();
			Db4oUnit.Assert.AreEqual(0, GenerateUUIDs());
			Db().Configure().GenerateUUIDs(-1);
			Db4oUnit.Assert.AreEqual(-1, GenerateUUIDs());
			Fixture().Config().GenerateUUIDs(0);
			Reopen();
			Db4oUnit.Assert.AreEqual(0, GenerateUUIDs());
		}

		private int GenerateUUIDs()
		{
			return ((com.db4o.@internal.LocalObjectContainer)Db()).Config().GenerateUUIDs();
		}
	}
}
