namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeUnionIntersect : com.db4o.@internal.btree.algebra.BTreeRangeUnionOperation
	{
		public BTreeRangeUnionIntersect(com.db4o.@internal.btree.BTreeRangeUnion union) : 
			base(union)
		{
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeSingle
			 range)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Intersect(_union, range);
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Intersect(_union, union);
		}
	}
}
