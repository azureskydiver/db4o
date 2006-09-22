namespace com.db4o.inside.btree
{
	public class BTreeRangeUnion : com.db4o.inside.btree.BTreeRange
	{
		private readonly com.db4o.inside.btree.BTreeRangeSingle[] _ranges;

		public BTreeRangeUnion(com.db4o.inside.btree.BTreeRangeSingle[] ranges) : this(ToSortedCollection
			(ranges))
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

		public virtual void Accept(com.db4o.inside.btree.BTreeRangeVisitor visitor)
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

		private static com.db4o.foundation.SortedCollection4 ToSortedCollection(com.db4o.inside.btree.BTreeRangeSingle[]
			 ranges)
		{
			if (null == ranges)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.foundation.SortedCollection4 collection = new com.db4o.foundation.SortedCollection4
				(com.db4o.inside.btree.BTreeRangeSingle.COMPARISON);
			for (int i = 0; i < ranges.Length; i++)
			{
				com.db4o.inside.btree.BTreeRangeSingle range = ranges[i];
				if (!range.IsEmpty())
				{
					collection.Add(range);
				}
			}
			return collection;
		}

		private static com.db4o.inside.btree.BTreeRangeSingle[] ToArray(com.db4o.foundation.SortedCollection4
			 collection)
		{
			return (com.db4o.inside.btree.BTreeRangeSingle[])collection.ToArray(new com.db4o.inside.btree.BTreeRangeSingle
				[collection.Size()]);
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToFirst()
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToLast()
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToLastOf(com.db4o.inside.btree.BTreeRange
			 upperRange)
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual com.db4o.inside.btree.BTreeRange Greater()
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRange
			 range)
		{
			if (null == range)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.inside.btree.algebra.BTreeRangeUnionIntersect(this).Dispatch(
				range);
		}

		public virtual com.db4o.foundation.Iterator4 Pointers()
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
				return ((com.db4o.inside.btree.BTreeRange)range).Pointers();
			}

			private readonly BTreeRangeUnion _enclosing;
		}

		public virtual com.db4o.foundation.Iterator4 Keys()
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
				return ((com.db4o.inside.btree.BTreeRange)range).Keys();
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

		public virtual com.db4o.inside.btree.BTreeRange Smaller()
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRange
			 other)
		{
			if (null == other)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.inside.btree.algebra.BTreeRangeUnionUnion(this).Dispatch(other
				);
		}

		public virtual com.db4o.foundation.Iterator4 Ranges()
		{
			return new com.db4o.foundation.ArrayIterator4(_ranges);
		}
	}
}
