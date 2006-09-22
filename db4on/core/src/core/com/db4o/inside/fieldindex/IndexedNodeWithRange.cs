namespace com.db4o.inside.fieldindex
{
	public interface IndexedNodeWithRange : com.db4o.inside.fieldindex.IndexedNode
	{
		com.db4o.inside.btree.BTreeRange GetRange();
	}
}
