namespace com.db4o.@internal.btree.algebra
{
	/// <exclude></exclude>
	public class BTreeRangeSingleUnion : com.db4o.@internal.btree.algebra.BTreeRangeSingleOperation
	{
		public BTreeRangeSingleUnion(com.db4o.@internal.btree.BTreeRangeSingle single) : 
			base(single)
		{
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeSingle
			 single)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Union(_single, single);
		}

		protected override com.db4o.@internal.btree.BTreeRange Execute(com.db4o.@internal.btree.BTreeRangeUnion
			 union)
		{
			return com.db4o.@internal.btree.algebra.BTreeAlgebra.Union(union, _single);
		}
	}
}
