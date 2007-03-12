namespace com.db4o.db4ounit.common.header
{
	public class ConfigurationSettingsTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public virtual void TestChangingUuidSettings()
		{
			Fixture().Config().GenerateUUIDs(0);
			Reopen();
			Db4oUnit.Assert.AreEqual(com.db4o.config.ConfigScope.GLOBALLY, GenerateUUIDs());
			Db().Configure().GenerateUUIDs(-1);
			Db4oUnit.Assert.AreEqual(com.db4o.config.ConfigScope.DISABLED, GenerateUUIDs());
			Fixture().Config().GenerateUUIDs(0);
			Reopen();
			Db4oUnit.Assert.AreEqual(com.db4o.config.ConfigScope.GLOBALLY, GenerateUUIDs());
		}

		private com.db4o.config.ConfigScope GenerateUUIDs()
		{
			return ((com.db4o.@internal.LocalObjectContainer)Db()).Config().GenerateUUIDs();
		}
	}
}
