namespace com.db4o.db4ounit.common.fieldindex
{
	/// <exclude></exclude>
	public class StringIndexTestCase : com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.StringIndexTestCase().RunSolo();
		}

		public virtual void TestNotEquals()
		{
			Add("foo");
			Add("bar");
			Add("baz");
			Add(null);
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item)
				);
			query.Descend("name").Constrain("bar").Not();
			AssertItems(new string[] { "foo", "baz", null }, query.Execute());
		}

		public virtual void TestCancelRemovalRollback()
		{
			PrepareCancelRemoval(Trans(), "original");
			Rename("original", "updated");
			Db().Rollback();
			GrafittiFreeSpace();
			Reopen();
			AssertExists("original");
		}

		public virtual void TestCancelRemovalRollbackForMultipleTransactions()
		{
			com.db4o.@internal.Transaction trans1 = NewTransaction();
			com.db4o.@internal.Transaction trans2 = NewTransaction();
			PrepareCancelRemoval(trans1, "original");
			AssertExists(trans2, "original");
			trans1.Rollback();
			AssertExists(trans2, "original");
			Add(trans2, "second");
			AssertExists(trans2, "original");
			trans2.Commit();
			AssertExists(trans2, "original");
			GrafittiFreeSpace();
			Reopen();
			AssertExists("original");
		}

		public virtual void TestCancelRemoval()
		{
			PrepareCancelRemoval(Trans(), "original");
			Db().Commit();
			GrafittiFreeSpace();
			Reopen();
			AssertExists("original");
		}

		private void PrepareCancelRemoval(com.db4o.@internal.Transaction transaction, string
			 itemName)
		{
			Add(itemName);
			Db().Commit();
			Rename(transaction, itemName, "updated");
			AssertExists(transaction, "updated");
			Rename(transaction, "updated", itemName);
			AssertExists(transaction, itemName);
		}

		public virtual void TestCancelRemovalForMultipleTransactions()
		{
			com.db4o.@internal.Transaction trans1 = NewTransaction();
			com.db4o.@internal.Transaction trans2 = NewTransaction();
			PrepareCancelRemoval(trans1, "original");
			Rename(trans2, "original", "updated");
			trans1.Commit();
			GrafittiFreeSpace();
			Reopen();
			AssertExists("original");
		}

		public virtual void TestDeletingAndReaddingMember()
		{
			Add("original");
			AssertExists("original");
			Rename("original", "updated");
			AssertExists("updated");
			Db4oUnit.Assert.IsNull(Query("original"));
			Reopen();
			AssertExists("updated");
			Db4oUnit.Assert.IsNull(Query("original"));
		}
	}
}
