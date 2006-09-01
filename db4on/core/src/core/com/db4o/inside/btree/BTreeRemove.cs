namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeRemove : com.db4o.inside.btree.BTreePatch
	{
		private com.db4o.inside.btree.BTreeRemove _next;

		public BTreeRemove(com.db4o.Transaction transaction, object obj) : base(transaction
			, obj)
		{
		}

		protected virtual object Committed(com.db4o.inside.btree.BTree btree)
		{
			btree.NotifyRemoveListener(GetObject());
			return com.db4o.foundation.No4.INSTANCE;
		}

		public override string ToString()
		{
			return "(-) " + base.ToString();
		}

		public virtual void Append(com.db4o.inside.btree.BTreeRemove patch)
		{
			if (_transaction == patch._transaction)
			{
				throw new System.ArgumentException();
			}
			if (_next == null)
			{
				_next = patch;
			}
			else
			{
				_next.Append(patch);
			}
		}

		public override object Commit(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree)
		{
			if (_transaction == trans)
			{
				if (HasNext())
				{
					return _next;
				}
				return Committed(btree);
			}
			if (HasNext())
			{
				object newNext = _next.Commit(trans, btree);
				if (newNext is com.db4o.inside.btree.BTreeRemove)
				{
					_next = (com.db4o.inside.btree.BTreeRemove)newNext;
				}
				else
				{
					_next = null;
				}
			}
			return this;
		}

		private bool HasNext()
		{
			return _next != null;
		}

		public override bool IsRemove()
		{
			return true;
		}

		public override com.db4o.inside.btree.BTreePatch ForTransaction(com.db4o.Transaction
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

		public override object Rollback(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree)
		{
			if (_transaction == trans)
			{
				if (HasNext())
				{
					return _next;
				}
				return GetObject();
			}
			if (HasNext())
			{
				object newNext = _next.Rollback(trans, btree);
				if (newNext is com.db4o.inside.btree.BTreeRemove)
				{
					_next = (com.db4o.inside.btree.BTreeRemove)newNext;
				}
				else
				{
					_next = null;
				}
			}
			return this;
		}

		public virtual com.db4o.inside.btree.BTreeRemove RemoveFor(com.db4o.Transaction trans
			)
		{
			if (_transaction == trans)
			{
				return _next;
			}
			if (_next == null)
			{
				return this;
			}
			return _next.RemoveFor(trans);
		}
	}
}
