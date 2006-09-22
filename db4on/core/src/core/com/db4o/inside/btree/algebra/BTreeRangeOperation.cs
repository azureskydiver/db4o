namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeOperation : com.db4o.inside.btree.BTreeRangeVisitor
	{
		private com.db4o.inside.btree.BTreeRange _resultingRange;

		public BTreeRangeOperation() : base()
		{
		}

		public virtual com.db4o.inside.btree.BTreeRange Dispatch(com.db4o.inside.btree.BTreeRange
			 range)
		{
			range.Accept(this);
			return _resultingRange;
		}

		public void Visit(com.db4o.inside.btree.BTreeRangeSingle single)
		{
			_resultingRange = Execute(single);
		}

		public void Visit(com.db4o.inside.btree.BTreeRangeUnion union)
		{
			_resultingRange = Execute(union);
		}

		protected abstract com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeUnion
			 union);

		protected abstract com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeSingle
			 single);
	}
}
