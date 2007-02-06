namespace com.db4o.@internal.btree
{
	public abstract class BTreePatch
	{
		protected readonly com.db4o.@internal.Transaction _transaction;

		protected object _object;

		public BTreePatch(com.db4o.@internal.Transaction transaction, object obj)
		{
			_transaction = transaction;
			_object = obj;
		}

		public abstract object Commit(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree);

		public virtual bool IsRemove()
		{
			return false;
		}

		public abstract com.db4o.@internal.btree.BTreePatch ForTransaction(com.db4o.@internal.Transaction
			 trans);

		public virtual object GetObject()
		{
			return _object;
		}

		public abstract object Rollback(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree);

		public override string ToString()
		{
			if (_object == null)
			{
				return "[NULL]";
			}
			return _object.ToString();
		}

		public virtual bool IsAdd()
		{
			return false;
		}

		public abstract object Key(com.db4o.@internal.Transaction trans);
	}
}
