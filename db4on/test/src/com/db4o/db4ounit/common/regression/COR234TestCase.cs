namespace com.db4o.db4ounit.common.regression
{
	/// <exclude></exclude>
	public class COR234TestCase : Db4oUnit.TestCase
	{
		public virtual void Test()
		{
			com.db4o.Db4o.Configure().AllowVersionUpdates(false);
			Db4oUnit.Assert.Expect(typeof(com.db4o.ext.OldFormatException), new _AnonymousInnerClass20
				(this));
		}

		private sealed class _AnonymousInnerClass20 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass20(COR234TestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Run()
			{
				com.db4o.Db4o.OpenFile(this._enclosing.OldDatabaseFilePath());
			}

			private readonly COR234TestCase _enclosing;
		}

		protected virtual string OldDatabaseFilePath()
		{
			string oldFile = com.db4o.db4ounit.util.IOServices.BuildTempPath("old_db.yap");
			com.db4o.foundation.io.File4.Copy(com.db4o.db4ounit.util.WorkspaceServices.WorkspaceTestFilePath
				("db4oVersions/db4o_3.0.3"), oldFile);
			return oldFile;
		}
	}
}
