namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeRemove : com.db4o.inside.btree.BTreePatch
	{
		public BTreeRemove(com.db4o.Transaction transaction, object obj) : base(transaction
			, obj)
		{
		}

		protected override object Committed(com.db4o.inside.btree.BTree btree)
		{
			btree.NotifyRemoveListener(_object);
			return com.db4o.foundation.No4.INSTANCE;
		}

		protected override object GetObject()
		{
			return com.db4o.foundation.No4.INSTANCE;
		}

		protected override object RolledBack(com.db4o.inside.btree.BTree btree)
		{
			return _object;
		}

		public override string ToString()
		{
			return "-B " + base.ToString();
		}
	}
}
