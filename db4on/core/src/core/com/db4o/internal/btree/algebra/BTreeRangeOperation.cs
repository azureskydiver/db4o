namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeOperation : com.db4o.@internal.btree.BTreeRangeVisitor
	{
		private com.db4o.@internal.btree.BTreeRange _resultingRange;

		public BTreeRangeOperation() : base()
		{
		}

		public virtual com.db4o.@internal.btree.BTreeRange Dispatch(com.db4o.@internal.btree.BTreeRange
			 range)
		{
			range.Accept(this);
			return _resultingRange;
		}

		public void Visit(com.db4o.@internal.btree.BTreeRangeSingle single)
		{
			_resultingRange = Execute(single);
		}

		public void Visit(com.db4o.@internal.btree.BTreeRangeUnion union)
		{
			_resultingRange = Execute(union);
		}

		protected abstract com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeUnion
			 union);

		protected abstract com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeSingle
			 single);
	}
}
