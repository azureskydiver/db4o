namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class TreeString : com.db4o.foundation.Tree
	{
		public string _key;

		public TreeString(string a_key)
		{
			this._key = a_key;
		}

		protected override com.db4o.foundation.Tree ShallowCloneInternal(com.db4o.foundation.Tree
			 tree)
		{
			com.db4o.@internal.TreeString ts = (com.db4o.@internal.TreeString)base.ShallowCloneInternal
				(tree);
			ts._key = _key;
			return ts;
		}

		public override object ShallowClone()
		{
			return ShallowCloneInternal(new com.db4o.@internal.TreeString(_key));
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			return com.db4o.@internal.handlers.StringHandler.Compare(com.db4o.@internal.Const4
				.stringIO.Write(((com.db4o.@internal.TreeString)a_to)._key), com.db4o.@internal.Const4
				.stringIO.Write(_key));
		}

		public override object Key()
		{
			return _key;
		}
	}
}
