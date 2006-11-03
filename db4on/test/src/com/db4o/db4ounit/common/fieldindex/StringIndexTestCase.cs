namespace com.db4o.db4ounit.common.fieldindex
{
	/// <exclude></exclude>
	public class StringIndexTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.StringIndexTestCase().RunSolo();
		}

		public class Item
		{
			public string name;

			public Item()
			{
			}

			public Item(string name_)
			{
				name = name_;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item)
				, "name");
		}

		public virtual void TestNotEquals()
		{
			Add("foo");
			Add("bar");
			Add("baz");
			Add(null);
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item)
				);
			query.Descend("name").Constrain("bar").Not();
			AssertItems(new string[] { "foo", "baz", null }, query.Execute());
		}

		private void AssertItems(string[] expected, com.db4o.ObjectSet result)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(ToObjectArray(expected));
			while (result.HasNext())
			{
				expectingVisitor.Visit(((com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item
					)result.Next()).name);
			}
			expectingVisitor.AssertExpectations();
		}

		private object[] ToObjectArray(string[] source)
		{
			object[] array = new object[source.Length];
			System.Array.Copy(source, 0, array, 0, source.Length);
			return array;
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
			com.db4o.Transaction trans1 = NewTransaction();
			com.db4o.Transaction trans2 = NewTransaction();
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

		private void PrepareCancelRemoval(com.db4o.Transaction transaction, string itemName
			)
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
			com.db4o.Transaction trans1 = NewTransaction();
			com.db4o.Transaction trans2 = NewTransaction();
			PrepareCancelRemoval(trans1, "original");
			Rename(trans2, "original", "updated");
			trans1.Commit();
			GrafittiFreeSpace();
			Reopen();
			AssertExists("original");
		}

		private void GrafittiFreeSpace()
		{
			com.db4o.YapRandomAccessFile file = ((com.db4o.YapRandomAccessFile)Db());
			com.db4o.inside.freespace.FreespaceManagerRam fm = (com.db4o.inside.freespace.FreespaceManagerRam
				)file.FreespaceManager();
			fm.TraverseFreeSlots(new _AnonymousInnerClass133(this, file));
		}

		private sealed class _AnonymousInnerClass133 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass133(StringIndexTestCase _enclosing, com.db4o.YapRandomAccessFile
				 file)
			{
				this._enclosing = _enclosing;
				this.file = file;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.slots.Slot slot = (com.db4o.inside.slots.Slot)obj;
				file.WriteXBytes(slot.GetAddress(), slot.GetLength());
			}

			private readonly StringIndexTestCase _enclosing;

			private readonly com.db4o.YapRandomAccessFile file;
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

		private void AssertExists(string itemName)
		{
			AssertExists(Trans(), itemName);
		}

		private void Add(string itemName)
		{
			Add(Trans(), itemName);
		}

		private void Add(com.db4o.Transaction transaction, string itemName)
		{
			Stream().Set(transaction, new com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item
				(itemName));
		}

		private void AssertExists(com.db4o.Transaction transaction, string itemName)
		{
			Db4oUnit.Assert.IsNotNull(Query(transaction, itemName));
		}

		private void Rename(com.db4o.Transaction transaction, string from, string to)
		{
			com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item item = Query(transaction
				, from);
			Db4oUnit.Assert.IsNotNull(item);
			item.name = to;
			Stream().Set(transaction, item);
		}

		private void Rename(string from, string to)
		{
			Rename(Trans(), from, to);
		}

		private com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item Query(string
			 name)
		{
			return Query(Trans(), name);
		}

		private com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item Query(com.db4o.Transaction
			 transaction, string name)
		{
			com.db4o.ObjectSet objectSet = NewQuery(transaction, name).Execute();
			if (!objectSet.HasNext())
			{
				return null;
			}
			return (com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item)objectSet.Next
				();
		}

		private com.db4o.query.Query NewQuery(com.db4o.Transaction transaction, string itemName
			)
		{
			com.db4o.query.Query query = Stream().Query(transaction);
			query.Constrain(typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCase.Item)
				);
			query.Descend("name").Constrain(itemName);
			return query;
		}
	}
}
