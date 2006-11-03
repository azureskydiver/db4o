namespace com.db4o.db4ounit.common.assorted
{
	public class SystemInfoTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
		}

		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.assorted.SystemInfoTestCase().RunSolo();
		}

		protected override void Db4oCustomTearDown()
		{
			com.db4o.Db4o.Configure().Freespace().UseRamSystem();
		}

		public virtual void TestDefaultFreespaceInfo()
		{
			AssertFreespaceInfo(Db().SystemInfo());
		}

		public virtual void TestIndexBasedFreespaceInfo()
		{
			com.db4o.Db4o.Configure().Freespace().UseIndexSystem();
			Reopen();
			AssertFreespaceInfo(Db().SystemInfo());
		}

		private void AssertFreespaceInfo(com.db4o.ext.SystemInfo info)
		{
			Db4oUnit.Assert.IsNotNull(info);
			com.db4o.db4ounit.common.assorted.SystemInfoTestCase.Item item = new com.db4o.db4ounit.common.assorted.SystemInfoTestCase.Item
				();
			Db().Set(item);
			Db().Commit();
			Db().Delete(item);
			Db().Commit();
			Db4oUnit.Assert.IsTrue(info.FreespaceEntryCount() > 0);
			Db4oUnit.Assert.IsTrue(info.FreespaceSize() > 0);
		}

		public virtual void TestTotalSize()
		{
			if (Fixture() is Db4oUnit.Extensions.Fixtures.AbstractFileBasedDb4oFixture)
			{
				Db4oUnit.Extensions.Fixtures.AbstractFileBasedDb4oFixture fixture = (Db4oUnit.Extensions.Fixtures.AbstractFileBasedDb4oFixture
					)Fixture();
				j4o.io.File f = new j4o.io.File(fixture.GetAbsolutePath());
				long expectedSize = f.Length();
				long actual = Db().SystemInfo().TotalSize();
				Db4oUnit.Assert.AreEqual(expectedSize, actual);
			}
		}
	}
}
