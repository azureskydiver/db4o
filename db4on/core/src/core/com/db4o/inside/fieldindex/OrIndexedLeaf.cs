namespace com.db4o.inside.fieldindex
{
	public class OrIndexedLeaf : com.db4o.inside.fieldindex.JoinedLeaf
	{
		public OrIndexedLeaf(com.db4o.QCon constraint, com.db4o.inside.fieldindex.IndexedNodeWithRange
			 leaf1, com.db4o.inside.fieldindex.IndexedNodeWithRange leaf2) : base(constraint
			, leaf1, leaf1.GetRange().Union(leaf2.GetRange()))
		{
		}
	}
}
