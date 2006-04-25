namespace com.db4o.inside.btree
{
	public class BTreeAdd : com.db4o.inside.btree.BTreePatch
	{
		public BTreeAdd(com.db4o.Transaction transaction, object obj) : base(transaction, 
			obj)
		{
		}

		public override object getObject(com.db4o.Transaction trans)
		{
			if (trans == _transaction)
			{
				return _object;
			}
			return base.getObject(trans);
		}
	}
}
