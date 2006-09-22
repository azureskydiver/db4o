namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public abstract class BTreeRangeUnionOperation : com.db4o.inside.btree.algebra.BTreeRangeOperation
	{
		protected readonly com.db4o.inside.btree.BTreeRangeUnion _union;

		public BTreeRangeUnionOperation(com.db4o.inside.btree.BTreeRangeUnion union)
		{
			_union = union;
		}
	}
}
