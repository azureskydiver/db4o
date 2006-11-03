namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public abstract class AbstractQueryResult : com.db4o.inside.query.QueryResult
	{
		protected readonly com.db4o.Transaction _transaction;

		public AbstractQueryResult(com.db4o.Transaction transaction)
		{
			_transaction = transaction;
		}

		public object Activate(object obj)
		{
			com.db4o.YapStream stream = Stream();
			stream.Activate1(_transaction, obj, stream.ConfigImpl().ActivationDepth());
			return obj;
		}

		public object ActivatedObject(int id)
		{
			com.db4o.YapStream stream = Stream();
			object ret = stream.GetActivatedObjectFromCache(_transaction, id);
			if (ret != null)
			{
				return ret;
			}
			return stream.ReadActivatedObjectNotInCache(_transaction, id);
		}

		public virtual object StreamLock()
		{
			com.db4o.YapStream stream = Stream();
			stream.CheckClosed();
			return stream.Lock();
		}

		public virtual com.db4o.YapStream Stream()
		{
			return _transaction.Stream();
		}

		public virtual com.db4o.Transaction Transaction()
		{
			return _transaction;
		}

		public virtual com.db4o.ext.ExtObjectContainer ObjectContainer()
		{
			return Stream();
		}

		public virtual System.Collections.IEnumerator GetEnumerator()
		{
			return new _AnonymousInnerClass55(this, IterateIDs());
		}

		private sealed class _AnonymousInnerClass55 : com.db4o.foundation.MappingIterator
		{
			public _AnonymousInnerClass55(AbstractQueryResult _enclosing, com.db4o.foundation.IntIterator4
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

		public virtual com.db4o.inside.query.AbstractQueryResult SupportSize()
		{
			return this;
		}

		public virtual com.db4o.inside.query.AbstractQueryResult SupportSort()
		{
			return this;
		}

		public virtual com.db4o.inside.query.AbstractQueryResult SupportElementAccess()
		{
			return this;
		}

		protected virtual int KnownSize()
		{
			return Size();
		}

		public virtual com.db4o.inside.query.AbstractQueryResult ToIdList()
		{
			com.db4o.inside.query.IdListQueryResult res = new com.db4o.inside.query.IdListQueryResult
				(Transaction(), KnownSize());
			com.db4o.foundation.IntIterator4 i = IterateIDs();
			while (i.MoveNext())
			{
				res.Add(i.CurrentInt());
			}
			return res;
		}

		protected virtual com.db4o.inside.query.AbstractQueryResult ToIdTree()
		{
			return new com.db4o.inside.query.IdTreeQueryResult(Transaction(), this);
		}

		public abstract object Get(int arg1);

		public abstract int IndexOf(int arg1);

		public abstract com.db4o.foundation.IntIterator4 IterateIDs();

		public abstract void LoadFromClassIndex(com.db4o.YapClass arg1);

		public abstract void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator arg1
			);

		public abstract void LoadFromIdReader(com.db4o.YapReader arg1);

		public abstract void LoadFromQuery(com.db4o.QQuery arg1);

		public abstract int Size();

		public abstract void Sort(com.db4o.query.QueryComparator arg1);
	}
}
