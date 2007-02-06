namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class IdListQueryResult : com.db4o.@internal.query.result.AbstractQueryResult
		, com.db4o.foundation.Visitor4
	{
		private com.db4o.foundation.Tree _candidates;

		private bool _checkDuplicates;

		public com.db4o.foundation.IntArrayList _ids;

		public IdListQueryResult(com.db4o.@internal.Transaction trans, int initialSize) : 
			base(trans)
		{
			_ids = new com.db4o.foundation.IntArrayList(initialSize);
		}

		public IdListQueryResult(com.db4o.@internal.Transaction trans) : this(trans, 0)
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
				return ActivatedObject(GetId(index));
			}
		}

		public override int GetId(int index)
		{
			if (index < 0 || index >= Size())
			{
				throw new System.IndexOutOfRangeException();
			}
			return _ids.Get(index);
		}

		public void CheckDuplicates()
		{
			_checkDuplicates = true;
		}

		public virtual void Visit(object a_tree)
		{
			com.db4o.@internal.query.processor.QCandidate candidate = (com.db4o.@internal.query.processor.QCandidate
				)a_tree;
			if (candidate.Include())
			{
				AddKeyCheckDuplicates(candidate._key);
			}
		}

		public virtual void AddKeyCheckDuplicates(int a_key)
		{
			if (_checkDuplicates)
			{
				com.db4o.@internal.TreeInt newNode = new com.db4o.@internal.TreeInt(a_key);
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
			com.db4o.foundation.Algorithms4.Qsort(new _AnonymousInnerClass73(this, cmp));
		}

		private sealed class _AnonymousInnerClass73 : com.db4o.foundation.QuickSortable4
		{
			public _AnonymousInnerClass73(IdListQueryResult _enclosing, com.db4o.query.QueryComparator
				 cmp)
			{
				this._enclosing = _enclosing;
				this.cmp = cmp;
			}

			public void Swap(int leftIndex, int rightIndex)
			{
				this._enclosing._ids.Swap(leftIndex, rightIndex);
			}

			public int Size()
			{
				return this._enclosing.Size();
			}

			public int Compare(int leftIndex, int rightIndex)
			{
				return cmp.Compare(this._enclosing.Get(leftIndex), this._enclosing.Get(rightIndex
					));
			}

			private readonly IdListQueryResult _enclosing;

			private readonly com.db4o.query.QueryComparator cmp;
		}

		public override void LoadFromClassIndex(com.db4o.@internal.ClassMetadata clazz)
		{
			com.db4o.@internal.classindex.ClassIndexStrategy index = clazz.Index();
			if (index is com.db4o.@internal.classindex.BTreeClassIndexStrategy)
			{
				com.db4o.@internal.btree.BTree btree = ((com.db4o.@internal.classindex.BTreeClassIndexStrategy
					)index).Btree();
				_ids = new com.db4o.foundation.IntArrayList(btree.Size(Transaction()));
			}
			index.TraverseAll(_transaction, new _AnonymousInnerClass92(this));
		}

		private sealed class _AnonymousInnerClass92 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass92(IdListQueryResult _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.Add(((int)a_object));
			}

			private readonly IdListQueryResult _enclosing;
		}

		public override void LoadFromQuery(com.db4o.@internal.query.processor.QQuery query
			)
		{
			query.ExecuteLocal(this);
		}

		public override void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 iter)
		{
			com.db4o.foundation.Tree.ByRef duplicates = new com.db4o.foundation.Tree.ByRef();
			while (iter.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yapClass = iter.CurrentClass();
				if (yapClass.GetName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
					if (claxx == null || !(Stream().i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx
						)))
					{
						com.db4o.@internal.classindex.ClassIndexStrategy index = yapClass.Index();
						index.TraverseAll(_transaction, new _AnonymousInnerClass115(this, duplicates));
					}
				}
			}
		}

		private sealed class _AnonymousInnerClass115 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass115(IdListQueryResult _enclosing, com.db4o.foundation.Tree.ByRef
				 duplicates)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				com.db4o.@internal.TreeInt newNode = new com.db4o.@internal.TreeInt(id);
				duplicates.value = com.db4o.foundation.Tree.Add(duplicates.value, newNode);
				if (newNode.Size() != 0)
				{
					this._enclosing.Add(id);
				}
			}

			private readonly IdListQueryResult _enclosing;

			private readonly com.db4o.foundation.Tree.ByRef duplicates;
		}

		public override void LoadFromIdReader(com.db4o.@internal.Buffer reader)
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
