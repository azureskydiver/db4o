namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TreeNodeIterator : com.db4o.foundation.AbstractTreeIterator
	{
		public TreeNodeIterator(com.db4o.foundation.Tree tree) : base(tree)
		{
		}

		protected override object CurrentValue(com.db4o.foundation.Tree tree)
		{
			return tree.Root();
		}
	}
}
