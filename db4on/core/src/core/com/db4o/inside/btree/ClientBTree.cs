namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class ClientBTree : com.db4o.inside.btree.BTree
	{
		public ClientBTree(int id, com.db4o.inside.ix.Indexable4 keyHandler, com.db4o.inside.ix.Indexable4
			 valueHandler) : base(id, keyHandler, valueHandler)
		{
		}
	}
}
