namespace com.db4o.@internal.fieldindex
{
	public interface IndexedNodeWithRange : com.db4o.@internal.fieldindex.IndexedNode
	{
		com.db4o.@internal.btree.BTreeRange GetRange();
	}
}
