namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public interface BTreeRangeVisitor
	{
		void Visit(com.db4o.inside.btree.BTreeRangeSingle range);

		void Visit(com.db4o.inside.btree.BTreeRangeUnion union);
	}
}
