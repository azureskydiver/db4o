namespace com.db4o.inside.btree
{
	public abstract class BTreePatch
	{
		private com.db4o.inside.btree.BTreePatch _previous;

		private com.db4o.inside.btree.BTreePatch _next;

		internal readonly com.db4o.Transaction _transaction;

		internal readonly object _object;

		public BTreePatch(com.db4o.Transaction transaction, object obj)
		{
			_transaction = transaction;
			_object = obj;
		}

		public virtual com.db4o.inside.btree.BTreePatch forTransaction(com.db4o.Transaction
			 trans)
		{
			if (_transaction == trans)
			{
				return this;
			}
			if (_next == null)
			{
				return null;
			}
			return _next.forTransaction(trans);
		}

		public virtual object getObject(com.db4o.Transaction trans)
		{
			com.db4o.inside.btree.BTreePatch patch = forTransaction(trans);
			if (patch == null)
			{
				return com.db4o.Null.INSTANCE;
			}
			return patch.getObject(trans);
		}
	}
}
