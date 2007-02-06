namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeSingleOperation : com.db4o.@internal.btree.algebra.BTreeRangeOperation
	{
		protected readonly com.db4o.@internal.btree.BTreeRangeSingle _single;

		public BTreeRangeSingleOperation(com.db4o.@internal.btree.BTreeRangeSingle single
			)
		{
			_single = single;
		}
	}
}
