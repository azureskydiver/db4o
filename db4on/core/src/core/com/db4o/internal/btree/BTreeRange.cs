namespace com.db4o.@internal.btree
{
	public interface BTreeRange
	{
		/// <summary>
		/// Iterates through all the valid pointers in
		/// this range.
		/// </summary>
		/// <remarks>
		/// Iterates through all the valid pointers in
		/// this range.
		/// </remarks>
		/// <returns>an Iterator4 over BTreePointer value</returns>
		System.Collections.IEnumerator Pointers();

		System.Collections.IEnumerator Keys();

		int Size();

		com.db4o.@internal.btree.BTreeRange Greater();

		com.db4o.@internal.btree.BTreeRange Union(com.db4o.@internal.btree.BTreeRange other
			);

		com.db4o.@internal.btree.BTreeRange ExtendToLast();

		com.db4o.@internal.btree.BTreeRange Smaller();

		com.db4o.@internal.btree.BTreeRange ExtendToFirst();

		com.db4o.@internal.btree.BTreeRange Intersect(com.db4o.@internal.btree.BTreeRange
			 range);

		com.db4o.@internal.btree.BTreeRange ExtendToLastOf(com.db4o.@internal.btree.BTreeRange
			 upperRange);

		bool IsEmpty();

		void Accept(com.db4o.@internal.btree.BTreeRangeVisitor visitor);

		com.db4o.@internal.btree.BTreePointer LastPointer();
	}
}
