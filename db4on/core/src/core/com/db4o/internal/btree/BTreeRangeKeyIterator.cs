namespace com.db4o.@internal.btree
{
	internal class BTreeRangeKeyIterator : com.db4o.@internal.btree.AbstractBTreeRangeIterator
	{
		public BTreeRangeKeyIterator(com.db4o.@internal.btree.BTreeRangeSingle range) : base
			(range)
		{
		}

		public override object Current
		{
			get
			{
				return CurrentPointer().Key();
			}
		}
	}
}
