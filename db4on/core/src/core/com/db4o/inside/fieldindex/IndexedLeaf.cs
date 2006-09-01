namespace com.db4o.inside.fieldindex
{
	/// <exclude></exclude>
	public class IndexedLeaf : com.db4o.inside.fieldindex.IndexedNodeBase
	{
		private readonly com.db4o.inside.btree.BTreeRange _range;

		public IndexedLeaf(com.db4o.QConObject qcon) : base(qcon)
		{
			_range = Search();
		}

		private com.db4o.inside.btree.BTreeRange Search()
		{
			com.db4o.inside.btree.BTreeRange range = Search(Constraint().GetObject());
			com.db4o.inside.fieldindex.QEBitmap bitmap = com.db4o.inside.fieldindex.QEBitmap.
				ForQE(Constraint().i_evaluator);
			if (bitmap.TakeGreater())
			{
				if (bitmap.TakeEqual())
				{
					return range.ExtendToLast();
				}
				return range.Greater();
			}
			if (bitmap.TakeSmaller())
			{
				if (bitmap.TakeEqual())
				{
					return range.ExtendToFirst();
				}
				return range.Smaller();
			}
			return range;
		}

		public override int ResultSize()
		{
			return _range.Size();
		}

		public override com.db4o.TreeInt ToTreeInt()
		{
			return AddRangeToTree(null, _range);
		}

		public override com.db4o.foundation.KeyValueIterator Iterator()
		{
			return _range.Iterator();
		}

		public virtual com.db4o.inside.btree.BTreeRange GetRange()
		{
			return _range;
		}
	}
}
