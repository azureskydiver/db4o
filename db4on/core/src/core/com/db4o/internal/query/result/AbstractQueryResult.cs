namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public abstract class AbstractQueryResult : com.db4o.@internal.query.result.QueryResult
	{
		protected readonly com.db4o.@internal.Transaction _transaction;

		public AbstractQueryResult(com.db4o.@internal.Transaction transaction)
		{
			_transaction = transaction;
		}

		public object Activate(object obj)
		{
			Stream().Activate1(_transaction, obj, Config().ActivationDepth());
			return obj;
		}

		public object ActivatedObject(int id)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			object ret = stream.GetActivatedObjectFromCache(_transaction, id);
			if (ret != null)
			{
				return ret;
			}
			return stream.ReadActivatedObjectNotInCache(_transaction, id);
		}

		public virtual object StreamLock()
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			stream.CheckClosed();
			return stream.Lock();
		}

		public virtual com.db4o.@internal.ObjectContainerBase Stream()
		{
			return _transaction.Stream();
		}

		public virtual com.db4o.@internal.Transaction Transaction()
		{
			return _transaction;
		}

		public virtual com.db4o.ext.ExtObjectContainer ObjectContainer()
		{
			return Stream();
		}

		public virtual System.Collections.IEnumerator GetEnumerator()
		{
			return new _AnonymousInnerClass56(this, IterateIDs());
		}

		private sealed class _AnonymousInnerClass56 : com.db4o.foundation.MappingIterator
		{
			public _AnonymousInnerClass56(AbstractQueryResult _enclosing, com.db4o.foundation.IntIterator4
				 baseArg1) : base(baseArg1)
			{
				this._enclosing = _enclosing;
			}

			protected override object Map(object current)
			{
				if (current == null)
				{
					return com.db4o.foundation.MappingIterator.SKIP;
				}
				lock (this._enclosing.StreamLock())
				{
					object obj = this._enclosing.ActivatedObject(((int)current));
					if (obj == null)
					{
						return com.db4o.foundation.MappingIterator.SKIP;
					}
					return obj;
				}
			}

			private readonly AbstractQueryResult _enclosing;
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult SupportSize()
		{
			return this;
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult SupportSort()
		{
			return this;
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult SupportElementAccess
			()
		{
			return this;
		}

		protected virtual int KnownSize()
		{
			return Size();
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult ToIdList()
		{
			com.db4o.@internal.query.result.IdListQueryResult res = new com.db4o.@internal.query.result.IdListQueryResult
				(Transaction(), KnownSize());
			com.db4o.foundation.IntIterator4 i = IterateIDs();
			while (i.MoveNext())
			{
				res.Add(i.CurrentInt());
			}
			return res;
		}

		protected virtual com.db4o.@internal.query.result.AbstractQueryResult ToIdTree()
		{
			return new com.db4o.@internal.query.result.IdTreeQueryResult(Transaction(), IterateIDs
				());
		}

		public virtual com.db4o.@internal.Config4Impl Config()
		{
			return Stream().Config();
		}

		public virtual int Size()
		{
			throw new System.NotImplementedException();
		}

		public virtual void Sort(com.db4o.query.QueryComparator cmp)
		{
			throw new System.NotImplementedException();
		}

		public virtual object Get(int index)
		{
			throw new System.NotImplementedException();
		}

		public virtual int GetId(int i)
		{
			throw new System.NotImplementedException();
		}

		public virtual int IndexOf(int id)
		{
			throw new System.NotImplementedException();
		}

		public virtual void LoadFromClassIndex(com.db4o.@internal.ClassMetadata c)
		{
			throw new System.NotImplementedException();
		}

		public virtual void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 i)
		{
			throw new System.NotImplementedException();
		}

		public virtual void LoadFromIdReader(com.db4o.@internal.Buffer r)
		{
			throw new System.NotImplementedException();
		}

		public virtual void LoadFromQuery(com.db4o.@internal.query.processor.QQuery q)
		{
			throw new System.NotImplementedException();
		}

		public abstract com.db4o.foundation.IntIterator4 IterateIDs();
	}
}
