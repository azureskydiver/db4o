namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeString : com.db4o.Tree
	{
		public readonly string i_key;

		public TreeString(string a_key)
		{
			this.i_key = a_key;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return com.db4o.YapString.compare(com.db4o.YapConst.stringIO.write(((com.db4o.TreeString
				)a_to).i_key), com.db4o.YapConst.stringIO.write(i_key));
		}
	}
}
