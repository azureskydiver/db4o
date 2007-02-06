namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeUnionOperation : com.db4o.@internal.btree.algebra.BTreeRangeOperation
	{
		protected readonly com.db4o.@internal.btree.BTreeRangeUnion _union;

		public BTreeRangeUnionOperation(com.db4o.@internal.btree.BTreeRangeUnion union)
		{
			_union = union;
		}
	}
}
