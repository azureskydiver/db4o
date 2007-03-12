namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TreeKeyIterator : com.db4o.foundation.AbstractTreeIterator
	{
		public TreeKeyIterator(com.db4o.foundation.Tree tree) : base(tree)
		{
		}

		protected override object CurrentValue(com.db4o.foundation.Tree tree)
		{
			return tree.Key();
		}
	}
}
