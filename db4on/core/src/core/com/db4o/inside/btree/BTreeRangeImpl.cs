namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeRangeImpl : com.db4o.inside.btree.BTreeRange
	{
		private readonly com.db4o.Transaction _transaction;

		private readonly com.db4o.inside.btree.BTree _btree;

		private readonly com.db4o.inside.btree.BTreePointer _first;

		private readonly com.db4o.inside.btree.BTreePointer _end;

		public BTreeRangeImpl(com.db4o.Transaction transaction, com.db4o.inside.btree.BTree
			 btree, com.db4o.inside.btree.BTreePointer first, com.db4o.inside.btree.BTreePointer
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

		public virtual int Size()
		{
			int size = 0;
			com.db4o.foundation.KeyValueIterator i = Iterator();
			while (i.MoveNext())
			{
				++size;
			}
			return size;
		}

		public virtual com.db4o.foundation.KeyValueIterator Iterator()
		{
			return new com.db4o.inside.btree.BTreeRangeIterator(this);
		}

		public com.db4o.inside.btree.BTreePointer End()
		{
			return _end;
		}

		public virtual com.db4o.Transaction Transaction()
		{
			return _transaction;
		}

		public virtual com.db4o.inside.btree.BTreePointer First()
		{
			return _first;
		}

		public virtual com.db4o.inside.btree.BTreeRange Greater()
		{
			return NewBTreeRangeImpl(_end, null);
		}

		public virtual com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRange
			 other)
		{
			com.db4o.inside.btree.BTreeRangeImpl rangeImpl = CheckRangeArgument(other);
			if (InternalOverlaps(rangeImpl) || InternalAdjacent(rangeImpl))
			{
				return NewBTreeRangeImpl(com.db4o.inside.btree.BTreePointer.Min(_first, rangeImpl
					._first), com.db4o.inside.btree.BTreePointer.Max(_end, rangeImpl._end));
			}
			return null;
		}

		private bool InternalAdjacent(com.db4o.inside.btree.BTreeRangeImpl rangeImpl)
		{
			return com.db4o.inside.btree.BTreePointer.Equals(_end, rangeImpl._first) || com.db4o.inside.btree.BTreePointer
				.Equals(rangeImpl._end, _first);
		}

		public virtual bool Overlaps(com.db4o.inside.btree.BTreeRange other)
		{
			return InternalOverlaps(CheckRangeArgument(other));
		}

		private bool InternalOverlaps(com.db4o.inside.btree.BTreeRangeImpl y)
		{
			return FirstOverlaps(this, y) || FirstOverlaps(y, this);
		}

		private bool FirstOverlaps(com.db4o.inside.btree.BTreeRangeImpl x, com.db4o.inside.btree.BTreeRangeImpl
			 y)
		{
			return com.db4o.inside.btree.BTreePointer.LessThan(y._first, x._end) && com.db4o.inside.btree.BTreePointer
				.LessThan(x._first, y._end);
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToFirst()
		{
			return NewBTreeRangeImpl(FirstBTreePointer(), _end);
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToLast()
		{
			return NewBTreeRangeImpl(_first, null);
		}

		public virtual com.db4o.inside.btree.BTreeRange Smaller()
		{
			return NewBTreeRangeImpl(FirstBTreePointer(), _first);
		}

		private com.db4o.inside.btree.BTreeRange NewBTreeRangeImpl(com.db4o.inside.btree.BTreePointer
			 first, com.db4o.inside.btree.BTreePointer end)
		{
			return new com.db4o.inside.btree.BTreeRangeImpl(Transaction(), _btree, first, end
				);
		}

		private com.db4o.inside.btree.BTreePointer FirstBTreePointer()
		{
			return Btree().FirstPointer(Transaction());
		}

		private com.db4o.inside.btree.BTree Btree()
		{
			return _btree;
		}

		public virtual com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRange
			 range)
		{
			com.db4o.inside.btree.BTreeRangeImpl rangeImpl = CheckRangeArgument(range);
			com.db4o.inside.btree.BTreePointer first = com.db4o.inside.btree.BTreePointer.Max
				(_first, rangeImpl._first);
			com.db4o.inside.btree.BTreePointer end = com.db4o.inside.btree.BTreePointer.Min(_end
				, rangeImpl._end);
			return NewBTreeRangeImpl(first, end);
		}

		public virtual com.db4o.inside.btree.BTreeRange ExtendToLastOf(com.db4o.inside.btree.BTreeRange
			 range)
		{
			com.db4o.inside.btree.BTreeRangeImpl rangeImpl = CheckRangeArgument(range);
			return NewBTreeRangeImpl(_first, rangeImpl._end);
		}

		private com.db4o.inside.btree.BTreeRangeImpl CheckRangeArgument(com.db4o.inside.btree.BTreeRange
			 range)
		{
			if (null == range)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.inside.btree.BTreeRangeImpl rangeImpl = (com.db4o.inside.btree.BTreeRangeImpl
				)range;
			if (Btree() != rangeImpl.Btree())
			{
				throw new System.ArgumentException();
			}
			return rangeImpl;
		}
	}
}
