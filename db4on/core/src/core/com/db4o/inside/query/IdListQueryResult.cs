namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public class IdListQueryResult : com.db4o.inside.query.AbstractQueryResult, com.db4o.foundation.Visitor4
	{
		private com.db4o.foundation.Tree _candidates;

		private bool _checkDuplicates;

		private readonly com.db4o.foundation.IntArrayList _ids;

		public IdListQueryResult(com.db4o.Transaction trans, int initialSize) : base(trans
			)
		{
			_ids = new com.db4o.foundation.IntArrayList(initialSize);
		}

		public IdListQueryResult(com.db4o.Transaction trans) : this(trans, 0)
		{
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			return _ids.IntIterator();
		}

		public override object Get(int index)
		{
			lock (StreamLock())
			{
				if (index < 0 || index >= Size())
				{
					throw new System.IndexOutOfRangeException();
				}
				return ActivatedObject(_ids.Get(index));
			}
		}

		public void CheckDuplicates()
		{
			_checkDuplicates = true;
		}

		public virtual void Visit(object a_tree)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_tree;
			if (candidate.Include())
			{
				AddKeyCheckDuplicates(candidate._key);
			}
		}

		public virtual void AddKeyCheckDuplicates(int a_key)
		{
			if (_checkDuplicates)
			{
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(a_key);
				_candidates = com.db4o.foundation.Tree.Add(_candidates, newNode);
				if (newNode._size == 0)
				{
					return;
				}
			}
			Add(a_key);
		}

		public override void Sort(com.db4o.query.QueryComparator cmp)
		{
			Sort(cmp, 0, Size() - 1);
		}

		private void Sort(com.db4o.query.QueryComparator cmp, int from, int to)
		{
			if (to - from < 1)
			{
				return;
			}
			object pivot = Get(to);
			int left = from;
			int right = to;
			while (left < right)
			{
				while (left < right && cmp.Compare(pivot, Get(left)) < 0)
				{
					left++;
				}
				while (left < right && cmp.Compare(pivot, Get(right)) >= 0)
				{
					right--;
				}
				Swap(left, right);
			}
			Swap(to, right);
			Sort(cmp, from, right - 1);
			Sort(cmp, right + 1, to);
		}

		private void Swap(int left, int right)
		{
			_ids.Swap(left, right);
		}

		public override void LoadFromClassIndex(com.db4o.YapClass clazz)
		{
			com.db4o.inside.classindex.ClassIndexStrategy index = clazz.Index();
			index.TraverseAll(_transaction, new _AnonymousInnerClass105(this));
		}

		private sealed class _AnonymousInnerClass105 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass105(IdListQueryResult _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.Add(((int)a_object));
			}

			private readonly IdListQueryResult _enclosing;
		}

		public override void LoadFromQuery(com.db4o.QQuery query)
		{
			query.ExecuteLocal(this);
		}

		public override void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator iter
			)
		{
			com.db4o.foundation.Tree[] duplicates = new com.db4o.foundation.Tree[1];
			while (iter.MoveNext())
			{
				com.db4o.YapClass yapClass = iter.CurrentClass();
				if (yapClass.GetName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
					if (claxx == null || !(Stream().i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx
						)))
					{
						com.db4o.inside.classindex.ClassIndexStrategy index = yapClass.Index();
						index.TraverseAll(_transaction, new _AnonymousInnerClass128(this, duplicates));
					}
				}
			}
		}

		private sealed class _AnonymousInnerClass128 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass128(IdListQueryResult _enclosing, com.db4o.foundation.Tree[]
				 duplicates)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(id);
				duplicates[0] = com.db4o.foundation.Tree.Add(duplicates[0], newNode);
				if (newNode.Size() != 0)
				{
					this._enclosing.Add(id);
				}
			}

			private readonly IdListQueryResult _enclosing;

			private readonly com.db4o.foundation.Tree[] duplicates;
		}

		public override void LoadFromIdReader(com.db4o.YapReader reader)
		{
			int size = reader.ReadInt();
			for (int i = 0; i < size; i++)
			{
				Add(reader.ReadInt());
			}
		}

		public virtual void Add(int id)
		{
			_ids.Add(id);
		}

		public override int IndexOf(int id)
		{
			return _ids.IndexOf(id);
		}

		public override int Size()
		{
			return _ids.Size();
		}
	}
}
