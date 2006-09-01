namespace com.db4o.inside.fieldindex
{
	public class JoinedLeaf : com.db4o.inside.fieldindex.IndexedNode
	{
		private readonly com.db4o.inside.fieldindex.IndexedLeaf _leaf1;

		private readonly com.db4o.inside.btree.BTreeRange _range;

		public JoinedLeaf(com.db4o.inside.fieldindex.IndexedLeaf leaf1, com.db4o.inside.btree.BTreeRange
			 range)
		{
			_leaf1 = leaf1;
			_range = range;
		}

		public virtual com.db4o.foundation.KeyValueIterator Iterator()
		{
			return _range.Iterator();
		}

		public virtual com.db4o.TreeInt ToTreeInt()
		{
			return com.db4o.inside.fieldindex.IndexedNodeBase.AddRangeToTree(null, _range);
		}

		public virtual com.db4o.inside.btree.BTree GetIndex()
		{
			return _leaf1.GetIndex();
		}

		public virtual bool IsResolved()
		{
			return _leaf1.IsResolved();
		}

		public virtual com.db4o.inside.fieldindex.IndexedNode Resolve()
		{
			return com.db4o.inside.fieldindex.IndexedPath.NewParentPath(this, _leaf1.Constraint
				());
		}

		public virtual int ResultSize()
		{
			return _range.Size();
		}
	}
}
