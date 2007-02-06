namespace com.db4o.@internal.fieldindex
{
	public class OrIndexedLeaf : com.db4o.@internal.fieldindex.JoinedLeaf
	{
		public OrIndexedLeaf(com.db4o.@internal.query.processor.QCon constraint, com.db4o.@internal.fieldindex.IndexedNodeWithRange
			 leaf1, com.db4o.@internal.fieldindex.IndexedNodeWithRange leaf2) : base(constraint
			, leaf1, leaf1.GetRange().Union(leaf2.GetRange()))
		{
		}
	}
}
