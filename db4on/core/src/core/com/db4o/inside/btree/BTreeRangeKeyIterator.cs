namespace com.db4o.inside.btree
{
	internal class BTreeRangeKeyIterator : com.db4o.inside.btree.AbstractBTreeRangeIterator
	{
		public BTreeRangeKeyIterator(com.db4o.inside.btree.BTreeRangeSingle range) : base
			(range)
		{
		}

		public override object Current()
		{
			return CurrentPointer().Key();
		}
	}
}
