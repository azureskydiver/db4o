namespace com.db4o.@internal.btree
{
	public abstract class BTreeUpdate : com.db4o.@internal.btree.BTreePatch
	{
		protected com.db4o.@internal.btree.BTreeUpdate _next;

		public BTreeUpdate(com.db4o.@internal.Transaction transaction, object obj) : base
			(transaction, obj)
		{
		}

		protected virtual bool HasNext()
		{
			return _next != null;
		}

		public override com.db4o.@internal.btree.BTreePatch ForTransaction(com.db4o.@internal.Transaction
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

		public virtual com.db4o.@internal.btree.BTreeUpdate RemoveFor(com.db4o.@internal.Transaction
			 trans)
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

		public virtual void Append(com.db4o.@internal.btree.BTreeUpdate patch)
		{
			if (_transaction == patch._transaction)
			{
				throw new System.ArgumentException();
			}
			if (!HasNext())
			{
				_next = patch;
			}
			else
			{
				_next.Append(patch);
			}
		}

		protected virtual void ApplyKeyChange(object obj)
		{
			_object = obj;
			if (HasNext())
			{
				_next.ApplyKeyChange(obj);
			}
		}

		protected abstract void Committed(com.db4o.@internal.btree.BTree btree);

		public override object Commit(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree)
		{
			com.db4o.@internal.btree.BTreeUpdate patch = (com.db4o.@internal.btree.BTreeUpdate
				)ForTransaction(trans);
			if (patch is com.db4o.@internal.btree.BTreeCancelledRemoval)
			{
				object obj = patch.GetCommittedObject();
				ApplyKeyChange(obj);
			}
			return InternalCommit(trans, btree);
		}

		protected virtual object InternalCommit(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree)
		{
			if (_transaction == trans)
			{
				Committed(btree);
				if (HasNext())
				{
					return _next;
				}
				return GetCommittedObject();
			}
			if (HasNext())
			{
				SetNextIfPatch(_next.InternalCommit(trans, btree));
			}
			return this;
		}

		private void SetNextIfPatch(object newNext)
		{
			if (newNext is com.db4o.@internal.btree.BTreeUpdate)
			{
				_next = (com.db4o.@internal.btree.BTreeUpdate)newNext;
			}
			else
			{
				_next = null;
			}
		}

		protected abstract object GetCommittedObject();

		public override object Rollback(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
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
				SetNextIfPatch(_next.Rollback(trans, btree));
			}
			return this;
		}

		public override object Key(com.db4o.@internal.Transaction trans)
		{
			com.db4o.@internal.btree.BTreePatch patch = ForTransaction(trans);
			if (patch == null)
			{
				return GetObject();
			}
			if (patch.IsRemove())
			{
				return com.db4o.foundation.No4.INSTANCE;
			}
			return patch.GetObject();
		}
	}
}
