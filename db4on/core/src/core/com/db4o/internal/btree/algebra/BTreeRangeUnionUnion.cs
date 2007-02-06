namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeUnionUnion : com.db4o.@internal.btree.algebra.BTreeRangeUnionOperation
	{
		public BTreeRangeUnionUnion(com.db4o.@internal.btree.BTreeRangeUnion union) : base
			(union)
		{
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Union(_union, union);
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Union(_union, single);
		}
	}
}
