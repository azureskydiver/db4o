namespace com.db4o.db4ounit.common.assorted
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
				db.Set(new com.db4o.db4ounit.common.assorted.NonStaticConfigurationTestCase.Data(
					1));
			}
			finally
			{
				db.Close();
			}
			cfg = com.db4o.Db4o.NewConfiguration();
			db = com.db4o.Db4o.OpenFile(cfg, FILENAME);
			try
			{
				db.Set(new com.db4o.db4ounit.common.assorted.NonStaticConfigurationTestCase.Data(
					2));
				Db4oUnit.Assert.AreEqual(1, db.Query(typeof(com.db4o.db4ounit.common.assorted.NonStaticConfigurationTestCase.Data)
					).Size());
			}
			finally
			{
				db.Close();
			}
		}
	}
}
