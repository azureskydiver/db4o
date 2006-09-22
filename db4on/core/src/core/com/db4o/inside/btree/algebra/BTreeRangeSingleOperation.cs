namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeSingleOperation : com.db4o.inside.btree.algebra.BTreeRangeOperation
	{
		protected readonly com.db4o.inside.btree.BTreeRangeSingle _single;

		public BTreeRangeSingleOperation(com.db4o.inside.btree.BTreeRangeSingle single)
		{
			_single = single;
		}
	}
}
