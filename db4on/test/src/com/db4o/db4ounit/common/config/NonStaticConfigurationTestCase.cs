namespace com.db4o.db4ounit.common.config
{
	public class NonStaticConfigurationTestCase : Db4oUnit.TestCase
	{
		public class Data
		{
			public int id;

			public Data(int id)
			{
				this.id = id;
			}
		}

		private static readonly string FILENAME = "nonstaticcfg.yap";

		public virtual void TestOpenWithNonStaticConfiguration()
		{
			new j4o.io.File(FILENAME).Delete();
			com.db4o.config.Configuration cfg = com.db4o.Db4o.NewConfiguration();
			cfg.ReadOnly(true);
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(cfg, FILENAME);
			try
			{
				db.Set(new com.db4o.db4ounit.common.config.NonStaticConfigurationTestCase.Data(1)
					);
			}
			finally
			{
				db.Close();
			}
			cfg = com.db4o.Db4o.NewConfiguration();
			db = com.db4o.Db4o.OpenFile(cfg, FILENAME);
			try
			{
				db.Set(new com.db4o.db4ounit.common.config.NonStaticConfigurationTestCase.Data(2)
					);
				Db4oUnit.Assert.AreEqual(1, db.Query(typeof(com.db4o.db4ounit.common.config.NonStaticConfigurationTestCase.Data)
					).Size());
			}
			finally
			{
				db.Close();
			}
		}

		public virtual void TestIndependentObjectConfigs()
		{
			com.db4o.config.Configuration config = com.db4o.Db4o.NewConfiguration();
			com.db4o.config.ObjectClass objectConfig = config.ObjectClass(typeof(com.db4o.db4ounit.common.config.NonStaticConfigurationTestCase.Data)
				);
			objectConfig.Translate(new com.db4o.config.TNull());
			com.db4o.config.Configuration otherConfig = com.db4o.Db4o.NewConfiguration();
			Db4oUnit.Assert.AreNotSame(config, otherConfig);
			com.db4o.@internal.Config4Class otherObjectConfig = (com.db4o.@internal.Config4Class
				)otherConfig.ObjectClass(typeof(com.db4o.db4ounit.common.config.NonStaticConfigurationTestCase.Data)
				);
			Db4oUnit.Assert.AreNotSame(objectConfig, otherObjectConfig);
			Db4oUnit.Assert.IsNull(otherObjectConfig.GetTranslator());
		}
	}
}
