namespace com.db4o.inside.btree
{
	public abstract class BTreePatch
	{
		protected com.db4o.inside.btree.BTreePatch _previous;

		protected com.db4o.inside.btree.BTreePatch _next;

		protected readonly com.db4o.Transaction _transaction;

		internal readonly object _object;

		public BTreePatch(com.db4o.Transaction transaction, object obj)
		{
			_transaction = transaction;
			_object = obj;
		}

		public virtual com.db4o.inside.btree.BTreePatch Append(com.db4o.inside.btree.BTreePatch
			 patch)
		{
			if (_transaction == patch._transaction)
			{
				patch._next = _next;
				return patch;
			}
			if (_next == null)
			{
				_next = patch;
			}
			else
			{
				_next = _next.Append(patch);
			}
			return this;
		}

		public virtual object Commit(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree)
		{
			return Commit(trans, btree, true);
		}

		private object Commit(com.db4o.Transaction trans, com.db4o.inside.btree.BTree btree
			, bool firstInList)
		{
			if (_transaction == trans)
			{
				if (_next != null)
				{
					return _next;
				}
				return Committed(btree);
			}
			if (_next != null)
			{
				object newNext = _next.Commit(trans, btree, false);
				if (newNext is com.db4o.inside.btree.BTreePatch)
				{
					_next = (com.db4o.inside.btree.BTreePatch)newNext;
				}
				else
				{
					_next = null;
				}
			}
			return this;
		}

		protected abstract object Committed(com.db4o.inside.btree.BTree btree);

		public virtual com.db4o.inside.btree.BTreePatch ForTransaction(com.db4o.Transaction
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
			return _next.ForTransaction(trans);
		}

		public virtual object GetObject(com.db4o.Transaction trans)
		{
			com.db4o.inside.btree.BTreePatch patch = ForTransaction(trans);
			if (patch == null)
			{
				return com.db4o.foundation.No4.INSTANCE;
			}
			return patch.GetObject();
		}

		protected abstract object GetObject();

		public virtual object Rollback(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree)
		{
			return Rollback(trans, btree, true);
		}

		public virtual object Rollback(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree, bool firstInList)
		{
			if (_transaction == trans)
			{
				if (_next != null)
				{
					return _next;
				}
				return RolledBack(btree);
			}
			if (_next != null)
			{
				object newNext = _next.Rollback(trans, btree, false);
				if (newNext is com.db4o.inside.btree.BTreePatch)
				{
					_next = (com.db4o.inside.btree.BTreePatch)newNext;
				}
				else
				{
					_next = null;
				}
			}
			return this;
		}

		protected abstract object RolledBack(com.db4o.inside.btree.BTree btree);

		public override string ToString()
		{
			if (_object == null)
			{
				return "[NULL]";
			}
			return _object.ToString();
		}
	}
}
