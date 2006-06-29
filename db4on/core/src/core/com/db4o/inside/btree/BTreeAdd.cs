namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeAdd : com.db4o.inside.btree.BTreePatch
	{
		public BTreeAdd(com.db4o.Transaction transaction, object obj) : base(transaction, 
			obj)
		{
		}

		protected override object GetObject()
		{
			return _object;
		}

		protected override object Committed(com.db4o.inside.btree.BTree btree)
		{
			return _object;
		}

		protected override object RolledBack(com.db4o.inside.btree.BTree btree)
		{
			btree.NotifyRemoveListener(_object);
			return com.db4o.foundation.No4.INSTANCE;
		}

		public override string ToString()
		{
			return "+B " + base.ToString();
		}
	}
}
