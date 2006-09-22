namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeUnionUnion : com.db4o.inside.btree.algebra.BTreeRangeUnionOperation
	{
		public BTreeRangeUnionUnion(com.db4o.inside.btree.BTreeRangeUnion union) : base(union
			)
		{
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Union(_union, union);
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Union(_union, single);
		}
	}
}
