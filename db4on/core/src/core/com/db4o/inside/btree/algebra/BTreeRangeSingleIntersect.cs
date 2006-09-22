namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeSingleIntersect : com.db4o.inside.btree.algebra.BTreeRangeSingleOperation
	{
		public BTreeRangeSingleIntersect(com.db4o.inside.btree.BTreeRangeSingle single) : 
			base(single)
		{
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Intersect(_single, single);
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Intersect(union, _single);
		}
	}
}
