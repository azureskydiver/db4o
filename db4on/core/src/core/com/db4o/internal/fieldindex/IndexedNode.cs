namespace com.db4o.@internal.fieldindex
{
	public interface IndexedNode : System.Collections.IEnumerable
	{
		bool IsResolved();

		com.db4o.@internal.fieldindex.IndexedNode Resolve();

		com.db4o.@internal.btree.BTree GetIndex();

		int ResultSize();

		com.db4o.@internal.TreeInt ToTreeInt();
	}
}
