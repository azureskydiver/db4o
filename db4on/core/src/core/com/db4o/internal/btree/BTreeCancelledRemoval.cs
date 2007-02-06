namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class BTreeCancelledRemoval : com.db4o.@internal.btree.BTreeUpdate
	{
		private readonly object _newKey;

		public BTreeCancelledRemoval(com.db4o.@internal.Transaction transaction, object originalKey
			, object newKey, com.db4o.@internal.btree.BTreeUpdate existingPatches) : base(transaction
			, originalKey)
		{
			_newKey = newKey;
			if (null != existingPatches)
			{
				Append(existingPatches);
			}
		}

		protected override void Committed(com.db4o.@internal.btree.BTree btree)
		{
		}

		public override string ToString()
		{
			return "(u) " + base.ToString();
		}

		protected override object GetCommittedObject()
		{
			return _newKey;
		}
	}
}
