namespace com.db4o.inside.fieldindex
{
	public class AndIndexedLeaf : com.db4o.inside.fieldindex.JoinedLeaf
	{
		public AndIndexedLeaf(com.db4o.inside.fieldindex.IndexedLeaf leaf1, com.db4o.inside.fieldindex.IndexedLeaf
			 leaf2) : base(leaf1, leaf1.GetRange().Intersect(leaf2.GetRange()))
		{
		}
	}
}
