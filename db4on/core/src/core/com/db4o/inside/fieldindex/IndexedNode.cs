namespace com.db4o.inside.fieldindex
{
	public interface IndexedNode : System.Collections.IEnumerable
	{
		bool IsResolved();

		com.db4o.inside.fieldindex.IndexedNode Resolve();

		com.db4o.inside.btree.BTree GetIndex();

		int ResultSize();

		com.db4o.TreeInt ToTreeInt();
	}
}
