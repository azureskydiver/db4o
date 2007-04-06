using Db4objects.Db4o.Internal.Btree;

namespace Db4objects.Db4o.Internal.Btree
{
	public class BTreeRangePointerIterator : AbstractBTreeRangeIterator
	{
		public BTreeRangePointerIterator(BTreeRangeSingle range) : base(range)
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
