namespace com.db4o.@internal.btree
{
	public class BTreeRangeUnion : com.db4o.@internal.btree.BTreeRange
	{
		private readonly com.db4o.@internal.btree.BTreeRangeSingle[] _ranges;

		public BTreeRangeUnion(com.db4o.@internal.btree.BTreeRangeSingle[] ranges) : this
			(ToSortedCollection(ranges))
		{
		}

		public BTreeRangeUnion(com.db4o.foundation.SortedCollection4 sorted)
		{
			if (null == sorted)
			{
				throw new System.ArgumentNullException();
			}
			_ranges = ToArray(sorted);
		}

		public virtual void Accept(com.db4o.@internal.btree.BTreeRangeVisitor visitor)
		{
			visitor.Visit(this);
		}

		public virtual bool IsEmpty()
		{
			for (int i = 0; i < _ranges.Length; i++)
			{
				if (!_ranges[i].IsEmpty())
				{
					return false;
				}
			}
			return true;
		}

		private static com.db4o.foundation.SortedCollection4 ToSortedCollection(com.db4o.@internal.btree.BTreeRangeSingle[]
			 ranges)
		{
			if (null == ranges)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.foundation.SortedCollection4 collection = new com.db4o.foundation.SortedCollection4
				(com.db4o.@internal.btree.BTreeRangeSingle.COMPARISON);
			for (int i = 0; i < ranges.Length; i++)
			{
				com.db4o.@internal.btree.BTreeRangeSingle range = ranges[i];
				if (!range.IsEmpty())
				{
					collection.Add(range);
				}
			}
			return collection;
		}

		private static com.db4o.@internal.btree.BTreeRangeSingle[] ToArray(com.db4o.foundation.SortedCollection4
			 collection)
		{
			return (com.db4o.@internal.btree.BTreeRangeSingle[])collection.ToArray(new com.db4o.@internal.btree.BTreeRangeSingle
				[collection.Size()]);
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToFirst()
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToLast()
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToLastOf(com.db4o.@internal.btree.BTreeRange
			 upperRange)
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.btree.BTreeRange Greater()
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.btree.BTreeRange Intersect(com.db4o.@internal.btree.BTreeRange
			 range)
		{
			if (null == range)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.@internal.btree.algebra.BTreeRangeUnionIntersect(this).Dispatch
				(range);
		}

		public virtual System.Collections.IEnumerator Pointers()
		{
			return com.db4o.foundation.Iterators.Concat(com.db4o.foundation.Iterators.Map(_ranges
				, new _AnonymousInnerClass76(this)));
		}

		private sealed class _AnonymousInnerClass76 : com.db4o.foundation.Function4
		{
			public _AnonymousInnerClass76(BTreeRangeUnion _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Apply(object range)
			{
				return ((com.db4o.@internal.btree.BTreeRange)range).Pointers();
			}

			private readonly BTreeRangeUnion _enclosing;
		}

		public virtual System.Collections.IEnumerator Keys()
		{
			return com.db4o.foundation.Iterators.Concat(com.db4o.foundation.Iterators.Map(_ranges
				, new _AnonymousInnerClass84(this)));
		}

		private sealed class _AnonymousInnerClass84 : com.db4o.foundation.Function4
		{
			public _AnonymousInnerClass84(BTreeRangeUnion _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Apply(object range)
			{
				return ((com.db4o.@internal.btree.BTreeRange)range).Keys();
			}

			private readonly BTreeRangeUnion _enclosing;
		}

		public virtual int Size()
		{
			int size = 0;
			for (int i = 0; i < _ranges.Length; i++)
			{
				size += _ranges[i].Size();
			}
			return size;
		}

		public virtual com.db4o.@internal.btree.BTreeRange Smaller()
		{
			throw new System.NotImplementedException();
		}

		public virtual com.db4o.@internal.btree.BTreeRange Union(com.db4o.@internal.btree.BTreeRange
			 other)
		{
			if (null == other)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.@internal.btree.algebra.BTreeRangeUnionUnion(this).Dispatch(other
				);
		}

		public virtual System.Collections.IEnumerator Ranges()
		{
			return new com.db4o.foundation.ArrayIterator4(_ranges);
		}

		public virtual com.db4o.@internal.btree.BTreePointer LastPointer()
		{
			throw new System.NotImplementedException();
		}
	}
}
