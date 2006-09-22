namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeUnionIntersect : com.db4o.inside.btree.algebra.BTreeRangeUnionOperation
	{
		public BTreeRangeUnionIntersect(com.db4o.inside.btree.BTreeRangeUnion union) : base
			(union)
		{
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeSingle
			 range)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Intersect(_union, range);
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Intersect(_union, union);
		}
	}
}
