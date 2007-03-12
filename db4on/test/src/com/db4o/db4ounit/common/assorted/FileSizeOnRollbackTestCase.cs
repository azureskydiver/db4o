namespace com.db4o.db4ounit.common.assorted
{
	public class FileSizeOnRollbackTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.FileSizeOnRollbackTestCase().RunSolo();
		}

		public class Item
		{
			public int _int;
		}

		public virtual void TestFileSizeDoesNotIncrease()
		{
			StoreSomeItems();
			ProduceSomeFreeSpace();
			int originalFileSize = FileSize();
			for (int i = 0; i < 100; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.FileSizeOnRollbackTestCase.Item());
				Db().Rollback();
				Db4oUnit.Assert.AreEqual(originalFileSize, FileSize());
			}
		}

		private void StoreSomeItems()
		{
			for (int i = 0; i < 3; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.FileSizeOnRollbackTestCase.Item());
			}
			Db().Commit();
		}

		private void ProduceSomeFreeSpace()
		{
			com.db4o.ObjectSet objectSet = NewQuery(typeof(com.db4o.db4ounit.common.assorted.FileSizeOnRollbackTestCase.Item)
				).Execute();
			while (objectSet.HasNext())
			{
				Db().Delete(objectSet.Next());
			}
			Db().Commit();
		}

		private int FileSize()
		{
			com.db4o.@internal.LocalObjectContainer localContainer = Fixture().FileSession();
			com.db4o.@internal.IoAdaptedObjectContainer container = (com.db4o.@internal.IoAdaptedObjectContainer
				)localContainer;
			container.SyncFiles();
			long length = new j4o.io.File(container.FileName()).Length();
			return (int)length;
		}
	}
}
