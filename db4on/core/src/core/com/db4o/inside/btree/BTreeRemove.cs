namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeRemove : com.db4o.inside.btree.BTreeUpdate
	{
		public BTreeRemove(com.db4o.Transaction transaction, object obj) : base(transaction
			, obj)
		{
		}

		protected override void Committed(com.db4o.inside.btree.BTree btree)
		{
			btree.NotifyRemoveListener(GetObject());
		}

		public override string ToString()
		{
			return "(-) " + base.ToString();
		}

		public override bool IsRemove()
		{
			return true;
		}

		protected override object GetCommittedObject()
		{
			return com.db4o.foundation.No4.INSTANCE;
		}
	}
}
