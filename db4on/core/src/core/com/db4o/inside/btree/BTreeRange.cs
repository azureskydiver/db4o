namespace com.db4o.inside.btree
{
	public interface BTreeRange
	{
		com.db4o.inside.btree.BTreePointer First();

		com.db4o.foundation.KeyValueIterator Iterator();

		int Size();

		com.db4o.inside.btree.BTreeRange Greater();

		com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRange other);

		com.db4o.inside.btree.BTreeRange ExtendToLast();

		com.db4o.inside.btree.BTreeRange Smaller();

		com.db4o.inside.btree.BTreeRange ExtendToFirst();

		com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRange range
			);

		com.db4o.inside.btree.BTreeRange ExtendToLastOf(com.db4o.inside.btree.BTreeRange 
			upperRange);

		bool Overlaps(com.db4o.inside.btree.BTreeRange range);
	}
}
