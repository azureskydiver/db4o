namespace com.db4o.@internal.fieldindex
{
	/// <exclude></exclude>
	public class IndexedLeaf : com.db4o.@internal.fieldindex.IndexedNodeBase, com.db4o.@internal.fieldindex.IndexedNodeWithRange
	{
		private readonly com.db4o.@internal.btree.BTreeRange _range;

		public IndexedLeaf(com.db4o.@internal.query.processor.QConObject qcon) : base(qcon
			)
		{
			_range = Search();
		}

		private com.db4o.@internal.btree.BTreeRange Search()
		{
			com.db4o.@internal.btree.BTreeRange range = Search(Constraint().GetObject());
			com.db4o.@internal.fieldindex.QEBitmap bitmap = com.db4o.@internal.fieldindex.QEBitmap
				.ForQE(Constraint().Evaluator());
			if (bitmap.TakeGreater())
			{
				if (bitmap.TakeEqual())
				{
					return range.ExtendToLast();
				}
				com.db4o.@internal.btree.BTreeRange greater = range.Greater();
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

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return _range.Keys();
		}

		public virtual com.db4o.@internal.btree.BTreeRange GetRange()
		{
			return _range;
		}
	}
}
