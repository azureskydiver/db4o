namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeSingleUnion : com.db4o.inside.btree.algebra.BTreeRangeSingleOperation
	{
		public BTreeRangeSingleUnion(com.db4o.inside.btree.BTreeRangeSingle single) : base
			(single)
		{
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Union(_single, single);
		}

		protected override com.db4o.inside.btree.BTreeRange Execute(com.db4o.inside.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.inside.btree.algebra.BTreeAlgebra.Union(union, _single);
		}
	}
}
