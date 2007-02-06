namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class BTreeAdd : com.db4o.@internal.btree.BTreePatch
	{
		public BTreeAdd(com.db4o.@internal.Transaction transaction, object obj) : base(transaction
			, obj)
		{
		}

		protected virtual object RolledBack(com.db4o.@internal.btree.BTree btree)
		{
			btree.NotifyRemoveListener(GetObject());
			return com.db4o.foundation.No4.INSTANCE;
		}

		public override string ToString()
		{
			return "(+) " + base.ToString();
		}

		public override object Commit(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree)
		{
			if (_transaction == trans)
			{
				return GetObject();
			}
			return this;
		}

		public override com.db4o.@internal.btree.BTreePatch ForTransaction(com.db4o.@internal.Transaction
			 trans)
		{
			if (_transaction == trans)
			{
				return this;
			}
			return null;
		}

		public override object Key(com.db4o.@internal.Transaction trans)
		{
			if (_transaction != trans)
			{
				return com.db4o.foundation.No4.INSTANCE;
			}
			return GetObject();
		}

		public override object Rollback(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree)
		{
			if (_transaction == trans)
			{
				return RolledBack(btree);
			}
			return this;
		}

		public override bool IsAdd()
		{
			return true;
		}
	}
}
