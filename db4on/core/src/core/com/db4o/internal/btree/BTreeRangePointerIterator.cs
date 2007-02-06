namespace com.db4o.@internal.btree
{
	public class BTreeRangePointerIterator : com.db4o.@internal.btree.AbstractBTreeRangeIterator
	{
		public BTreeRangePointerIterator(com.db4o.@internal.btree.BTreeRangeSingle range)
			 : base(range)
		{
		}

		public override object Current
		{
			get
			{
				return CurrentPointer();
			}
		}
	}
}
