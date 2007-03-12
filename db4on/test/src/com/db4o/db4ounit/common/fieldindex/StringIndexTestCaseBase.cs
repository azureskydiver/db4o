namespace com.db4o.db4ounit.common.fieldindex
{
	/// <exclude></exclude>
	public abstract class StringIndexTestCaseBase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
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

		public StringIndexTestCaseBase() : base()
		{
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item)
				, "name");
		}

		protected virtual void AssertItems(string[] expected, com.db4o.ObjectSet result)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(ToObjectArray(expected));
			while (result.HasNext())
			{
				expectingVisitor.Visit(((com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item
					)result.Next()).name);
			}
			expectingVisitor.AssertExpectations();
		}

		protected virtual object[] ToObjectArray(string[] source)
		{
			object[] array = new object[source.Length];
			System.Array.Copy(source, 0, array, 0, source.Length);
			return array;
		}

		protected virtual void GrafittiFreeSpace()
		{
			com.db4o.@internal.IoAdaptedObjectContainer file = ((com.db4o.@internal.IoAdaptedObjectContainer
				)Db());
			com.db4o.@internal.freespace.FreespaceManagerRam fm = (com.db4o.@internal.freespace.FreespaceManagerRam
				)file.FreespaceManager();
			fm.TraverseFreeSlots(new _AnonymousInnerClass58(this, file));
		}

		private sealed class _AnonymousInnerClass58 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass58(StringIndexTestCaseBase _enclosing, com.db4o.@internal.IoAdaptedObjectContainer
				 file)
			{
				this._enclosing = _enclosing;
				this.file = file;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.slots.Slot slot = (com.db4o.@internal.slots.Slot)obj;
				file.OverwriteDeletedBytes(slot.GetAddress(), slot.GetLength());
			}

			private readonly StringIndexTestCaseBase _enclosing;

			private readonly com.db4o.@internal.IoAdaptedObjectContainer file;
		}

		protected virtual void AssertExists(string itemName)
		{
			AssertExists(Trans(), itemName);
		}

		protected virtual void Add(string itemName)
		{
			Add(Trans(), itemName);
		}

		protected virtual void Add(com.db4o.@internal.Transaction transaction, string itemName
			)
		{
			Stream().Set(transaction, new com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item
				(itemName));
		}

		protected virtual void AssertExists(com.db4o.@internal.Transaction transaction, string
			 itemName)
		{
			Db4oUnit.Assert.IsNotNull(Query(transaction, itemName));
		}

		protected virtual void Rename(com.db4o.@internal.Transaction transaction, string 
			from, string to)
		{
			com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item item = Query(transaction
				, from);
			Db4oUnit.Assert.IsNotNull(item);
			item.name = to;
			Stream().Set(transaction, item);
		}

		protected virtual void Rename(string from, string to)
		{
			Rename(Trans(), from, to);
		}

		protected virtual com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item
			 Query(string name)
		{
			return Query(Trans(), name);
		}

		protected virtual com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item
			 Query(com.db4o.@internal.Transaction transaction, string name)
		{
			com.db4o.ObjectSet objectSet = NewQuery(transaction, name).Execute();
			if (!objectSet.HasNext())
			{
				return null;
			}
			return (com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item)objectSet
				.Next();
		}

		protected virtual com.db4o.query.Query NewQuery(com.db4o.@internal.Transaction transaction
			, string itemName)
		{
			com.db4o.query.Query query = Stream().Query(transaction);
			query.Constrain(typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCaseBase.Item)
				);
			query.Descend("name").Constrain(itemName);
			return query;
		}
	}
}
