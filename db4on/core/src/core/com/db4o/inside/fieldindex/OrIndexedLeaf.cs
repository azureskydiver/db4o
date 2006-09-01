namespace com.db4o.inside.fieldindex
{
	public class OrIndexedLeaf : com.db4o.inside.fieldindex.JoinedLeaf
	{
		public OrIndexedLeaf(com.db4o.inside.fieldindex.IndexedLeaf leaf1, com.db4o.inside.fieldindex.IndexedLeaf
			 leaf2) : base(leaf1, leaf1.GetRange().Union(leaf2.GetRange()))
		{
		}
	}
}
