namespace com.db4o
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
			com.db4o.TreeString ts = (com.db4o.TreeString)base.ShallowCloneInternal(tree);
			ts._key = _key;
			return ts;
		}

		public override object ShallowClone()
		{
			return ShallowCloneInternal(new com.db4o.TreeString(_key));
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			return com.db4o.YapString.Compare(com.db4o.YapConst.stringIO.Write(((com.db4o.TreeString
				)a_to)._key), com.db4o.YapConst.stringIO.Write(_key));
		}
	}
}
