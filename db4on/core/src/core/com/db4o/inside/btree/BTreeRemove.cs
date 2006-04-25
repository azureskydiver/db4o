namespace com.db4o.inside.btree
{
	public class BTreeRemove : com.db4o.inside.btree.BTreePatch
	{
		public BTreeRemove(com.db4o.Transaction transaction, object obj) : base(transaction
			, obj)
		{
		}

		public override object getObject(com.db4o.Transaction trans)
		{
			if (trans == _transaction)
			{
				return com.db4o.Null.INSTANCE;
			}
			return base.getObject(trans);
		}
	}
}
