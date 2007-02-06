namespace com.db4o.@internal.fieldindex
{
	public class JoinedLeaf : com.db4o.@internal.fieldindex.IndexedNodeWithRange
	{
		private readonly com.db4o.@internal.query.processor.QCon _constraint;

		private readonly com.db4o.@internal.fieldindex.IndexedNodeWithRange _leaf1;

		private readonly com.db4o.@internal.btree.BTreeRange _range;

		public JoinedLeaf(com.db4o.@internal.query.processor.QCon constraint, com.db4o.@internal.fieldindex.IndexedNodeWithRange
			 leaf1, com.db4o.@internal.btree.BTreeRange range)
		{
			if (null == constraint || null == leaf1 || null == range)
			{
				throw new System.ArgumentNullException();
			}
			_constraint = constraint;
			_leaf1 = leaf1;
			_range = range;
		}

		public virtual com.db4o.@internal.query.processor.QCon GetConstraint()
		{
			return _constraint;
		}

		public virtual com.db4o.@internal.btree.BTreeRange GetRange()
		{
			return _range;
		}

		public virtual System.Collections.IEnumerator GetEnumerator()
		{
			return _range.Keys();
		}

		public virtual com.db4o.@internal.TreeInt ToTreeInt()
		{
			return com.db4o.@internal.fieldindex.IndexedNodeBase.AddToTree(null, this);
		}

		public virtual com.db4o.@internal.btree.BTree GetIndex()
		{
			return _leaf1.GetIndex();
		}

		public virtual bool IsResolved()
		{
			return _leaf1.IsResolved();
		}

		public virtual com.db4o.@internal.fieldindex.IndexedNode Resolve()
		{
			return com.db4o.@internal.fieldindex.IndexedPath.NewParentPath(this, _constraint);
		}

		public virtual int ResultSize()
		{
			return _range.Size();
		}
	}
}
