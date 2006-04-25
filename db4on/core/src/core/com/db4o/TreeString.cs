namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeString : com.db4o.Tree
	{
		public string _key;

		public TreeString(string a_key)
		{
			this._key = a_key;
		}

		protected override com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.TreeString ts = (com.db4o.TreeString)base.shallowCloneInternal(tree);
			ts._key = _key;
			return ts;
		}

		public override object shallowClone()
		{
			return shallowCloneInternal(new com.db4o.TreeString(_key));
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return com.db4o.YapString.compare(com.db4o.YapConst.stringIO.write(((com.db4o.TreeString
				)a_to)._key), com.db4o.YapConst.stringIO.write(_key));
		}
	}
}
