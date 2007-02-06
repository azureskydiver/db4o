namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeSingleIntersect : com.db4o.@internal.btree.algebra.BTreeRangeSingleOperation
	{
		public BTreeRangeSingleIntersect(com.db4o.@internal.btree.BTreeRangeSingle single
			) : base(single)
		{
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Intersect(_single, single);
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Intersect(union, _single);
		}
	}
}
