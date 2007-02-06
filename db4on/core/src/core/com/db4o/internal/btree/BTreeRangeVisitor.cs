namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public interface BTreeRangeVisitor
	{
		void Visit(com.db4o.@internal.btree.BTreeRangeSingle range);

		void Visit(com.db4o.@internal.btree.BTreeRangeUnion union);
	}
}
