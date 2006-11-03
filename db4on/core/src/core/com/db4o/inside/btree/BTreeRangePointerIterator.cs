namespace com.db4o.inside.btree
{
	public class BTreeRangePointerIterator : com.db4o.inside.btree.AbstractBTreeRangeIterator
	{
		public BTreeRangePointerIterator(com.db4o.inside.btree.BTreeRangeSingle range) : 
			base(range)
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
