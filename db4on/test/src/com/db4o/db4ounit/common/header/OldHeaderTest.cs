namespace com.db4o.db4ounit.common.header
{
	public class OldHeaderTest : Db4oUnit.TestCase
	{
		private static readonly string ORIGINAL_FILE = com.db4o.db4ounit.util.WorkspaceServices
			.WorkspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2");

		private static readonly string DB_FILE = com.db4o.db4ounit.util.WorkspaceServices
			.WorkspacePath("db4oj.tests/test/db4oVersions/db4o_5.5.2.yap");

		public virtual void Test()
		{
			com.db4o.db4ounit.util.File4.Copy(ORIGINAL_FILE, DB_FILE);
			com.db4o.Db4o.Configure().AllowVersionUpdates(true);
			com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(DB_FILE);
			try
			{
				Db4oUnit.Assert.IsNotNull(oc);
			}
			finally
			{
				oc.Close();
				com.db4o.Db4o.Configure().AllowVersionUpdates(false);
			}
		}
	}
}
