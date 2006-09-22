namespace com.db4o.inside.fieldindex
{
	public class AndIndexedLeaf : com.db4o.inside.fieldindex.JoinedLeaf
	{
		public AndIndexedLeaf(com.db4o.QCon constraint, com.db4o.inside.fieldindex.IndexedNodeWithRange
			 leaf1, com.db4o.inside.fieldindex.IndexedNodeWithRange leaf2) : base(constraint
			, leaf1, leaf1.GetRange().Intersect(leaf2.GetRange()))
		{
		}
	}
}
