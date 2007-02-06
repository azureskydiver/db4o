namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class BTreeRangeSingle : com.db4o.@internal.btree.BTreeRange
	{
		private sealed class _AnonymousInnerClass14 : com.db4o.foundation.Comparison4
		{
			public _AnonymousInnerClass14()
			{
			}

			public int Compare(object x, object y)
			{
				com.db4o.@internal.btree.BTreeRangeSingle xRange = (com.db4o.@internal.btree.BTreeRangeSingle
					)x;
				com.db4o.@internal.btree.BTreeRangeSingle yRange = (com.db4o.@internal.btree.BTreeRangeSingle
					)y;
				return xRange.First().CompareTo(yRange.First());
			}
		}

		public static readonly com.db4o.foundation.Comparison4 COMPARISON = new _AnonymousInnerClass14
			();

		private readonly com.db4o.@internal.Transaction _transaction;

		private readonly com.db4o.@internal.btree.BTree _btree;

		private readonly com.db4o.@internal.btree.BTreePointer _first;

		private readonly com.db4o.@internal.btree.BTreePointer _end;

		public BTreeRangeSingle(com.db4o.@internal.Transaction transaction, com.db4o.@internal.btree.BTree
			 btree, com.db4o.@internal.btree.BTreePointer first, com.db4o.@internal.btree.BTreePointer
			 end)
		{
			if (transaction == null || btree == null)
			{
				throw new System.ArgumentNullException();
			}
			_transaction = transaction;
			_btree = btree;
			_first = first;
			_end = end;
		}

		public virtual void Accept(com.db4o.@internal.btree.BTreeRangeVisitor visitor)
		{
			visitor.Visit(this);
		}

		public virtual bool IsEmpty()
		{
			return com.db4o.@internal.btree.BTreePointer.Equals(_first, _end);
		}

		public virtual int Size()
		{
			if (IsEmpty())
			{
				return 0;
			}
			int size = 0;
			System.Collections.IEnumerator i = Keys();
			while (i.MoveNext())
			{
				++size;
			}
			return size;
		}

		public virtual System.Collections.IEnumerator Pointers()
		{
			return new com.db4o.@internal.btree.BTreeRangePointerIterator(this);
		}

		public virtual System.Collections.IEnumerator Keys()
		{
			return new com.db4o.@internal.btree.BTreeRangeKeyIterator(this);
		}

		public com.db4o.@internal.btree.BTreePointer End()
		{
			return _end;
		}

		public virtual com.db4o.@internal.Transaction Transaction()
		{
			return _transaction;
		}

		public virtual com.db4o.@internal.btree.BTreePointer First()
		{
			return _first;
		}

		public virtual com.db4o.@internal.btree.BTreeRange Greater()
		{
			return NewBTreeRangeSingle(_end, null);
		}

		public virtual com.db4o.@internal.btree.BTreeRange Union(com.db4o.@internal.btree.BTreeRange
			 other)
		{
			if (null == other)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.@internal.btree.algebra.BTreeRangeSingleUnion(this).Dispatch(
				other);
		}

		public virtual bool Adjacent(com.db4o.@internal.btree.BTreeRangeSingle range)
		{
			return com.db4o.@internal.btree.BTreePointer.Equals(_end, range._first) || com.db4o.@internal.btree.BTreePointer
				.Equals(range._end, _first);
		}

		public virtual bool Overlaps(com.db4o.@internal.btree.BTreeRangeSingle range)
		{
			return FirstOverlaps(this, range) || FirstOverlaps(range, this);
		}

		private bool FirstOverlaps(com.db4o.@internal.btree.BTreeRangeSingle x, com.db4o.@internal.btree.BTreeRangeSingle
			 y)
		{
			return com.db4o.@internal.btree.BTreePointer.LessThan(y._first, x._end) && com.db4o.@internal.btree.BTreePointer
				.LessThan(x._first, y._end);
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToFirst()
		{
			return NewBTreeRangeSingle(FirstBTreePointer(), _end);
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToLast()
		{
			return NewBTreeRangeSingle(_first, null);
		}

		public virtual com.db4o.@internal.btree.BTreeRange Smaller()
		{
			return NewBTreeRangeSingle(FirstBTreePointer(), _first);
		}

		public virtual com.db4o.@internal.btree.BTreeRangeSingle NewBTreeRangeSingle(com.db4o.@internal.btree.BTreePointer
			 first, com.db4o.@internal.btree.BTreePointer end)
		{
			return new com.db4o.@internal.btree.BTreeRangeSingle(Transaction(), _btree, first
				, end);
		}

		public virtual com.db4o.@internal.btree.BTreeRange NewEmptyRange()
		{
			return NewBTreeRangeSingle(null, null);
		}

		private com.db4o.@internal.btree.BTreePointer FirstBTreePointer()
		{
			return Btree().FirstPointer(Transaction());
		}

		private com.db4o.@internal.btree.BTree Btree()
		{
			return _btree;
		}

		public virtual com.db4o.@internal.btree.BTreeRange Intersect(com.db4o.@internal.btree.BTreeRange
			 range)
		{
			if (null == range)
			{
				throw new System.ArgumentNullException();
			}
			return new com.db4o.@internal.btree.algebra.BTreeRangeSingleIntersect(this).Dispatch
				(range);
		}

		public virtual com.db4o.@internal.btree.BTreeRange ExtendToLastOf(com.db4o.@internal.btree.BTreeRange
			 range)
		{
			com.db4o.@internal.btree.BTreeRangeSingle rangeImpl = CheckRangeArgument(range);
			return NewBTreeRangeSingle(_first, rangeImpl._end);
		}

		public override string ToString()
		{
			return "BTreeRangeSingle(first=" + _first + ", end=" + _end + ")";
		}

		private com.db4o.@internal.btree.BTreeRangeSingle CheckRangeArgument(com.db4o.@internal.btree.BTreeRange
			 range)
		{
			if (null == range)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.@internal.btree.BTreeRangeSingle rangeImpl = (com.db4o.@internal.btree.BTreeRangeSingle
				)range;
			if (Btree() != rangeImpl.Btree())
			{
				throw new System.ArgumentException();
			}
			return rangeImpl;
		}

		public virtual com.db4o.@internal.btree.BTreePointer LastPointer()
		{
			if (_end == null)
			{
				return Btree().LastPointer(Transaction());
			}
			return _end.Previous();
		}
	}
}
