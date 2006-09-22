namespace com.db4o.inside.fieldindex
{
	/// <exclude></exclude>
	public class IndexedLeaf : com.db4o.inside.fieldindex.IndexedNodeBase, com.db4o.inside.fieldindex.IndexedNodeWithRange
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
				ForQE(Constraint().Evaluator());
			if (bitmap.TakeGreater())
			{
				if (bitmap.TakeEqual())
				{
					return range.ExtendToLast();
				}
				com.db4o.inside.btree.BTreeRange greater = range.Greater();
				if (bitmap.TakeSmaller())
				{
					return greater.Union(range.Smaller());
				}
				return greater;
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

		public override com.db4o.foundation.Iterator4 Iterator()
		{
			return _range.Keys();
		}

		public virtual com.db4o.inside.btree.BTreeRange GetRange()
		{
			return _range;
		}
	}
}
