namespace com.db4o.db4ounit.common.header
{
	public class OldHeaderTest : Db4oUnit.TestCase
	{
		private static readonly string ORIGINAL_FILE = com.db4o.db4ounit.util.WorkspaceServices
			.WorkspaceTestFilePath("db4oVersions/db4o_5.5.2");

		private static readonly string DB_FILE = com.db4o.db4ounit.util.WorkspaceServices
			.WorkspaceTestFilePath("db4oVersions/db4o_5.5.2.yap");

		public virtual void Test()
		{
			if (!System.IO.File.Exists(ORIGINAL_FILE))
			{
				Db4oUnit.TestPlatform.EmitWarning(ORIGINAL_FILE + " does not exist. Can not run "
					 + GetType().FullName);
				return;
			}
			com.db4o.foundation.io.File4.Copy(ORIGINAL_FILE, DB_FILE);
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
