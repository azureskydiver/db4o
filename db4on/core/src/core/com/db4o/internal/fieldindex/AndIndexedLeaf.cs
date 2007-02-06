namespace com.db4o.@internal.fieldindex
{
	public class AndIndexedLeaf : com.db4o.@internal.fieldindex.JoinedLeaf
	{
		public AndIndexedLeaf(com.db4o.@internal.query.processor.QCon constraint, com.db4o.@internal.fieldindex.IndexedNodeWithRange
			 leaf1, com.db4o.@internal.fieldindex.IndexedNodeWithRange leaf2) : base(constraint
			, leaf1, leaf1.GetRange().Intersect(leaf2.GetRange()))
		{
		}
	}
}
