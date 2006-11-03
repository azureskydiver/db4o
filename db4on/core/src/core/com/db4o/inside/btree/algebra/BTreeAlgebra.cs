namespace com.db4o.inside.btree.algebra
{
	/// <exclude></exclude>
	internal class BTreeAlgebra
	{
		public static com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRangeUnion
			 union, com.db4o.inside.btree.BTreeRangeSingle single)
		{
			com.db4o.foundation.SortedCollection4 collection = NewBTreeRangeSingleCollection(
				);
			CollectIntersections(collection, union, single);
			return ToRange(collection);
		}

		public static com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRangeUnion
			 union1, com.db4o.inside.btree.BTreeRangeUnion union2)
		{
			com.db4o.foundation.SortedCollection4 collection = NewBTreeRangeSingleCollection(
				);
			System.Collections.IEnumerator ranges = union1.Ranges();
			while (ranges.MoveNext())
			{
				com.db4o.inside.btree.BTreeRangeSingle current = (com.db4o.inside.btree.BTreeRangeSingle
					)ranges.Current;
				CollectIntersections(collection, union2, current);
			}
			return ToRange(collection);
		}

		private static void CollectIntersections(com.db4o.foundation.SortedCollection4 collection
			, com.db4o.inside.btree.BTreeRangeUnion union, com.db4o.inside.btree.BTreeRangeSingle
			 single)
		{
			System.Collections.IEnumerator ranges = union.Ranges();
			while (ranges.MoveNext())
			{
				com.db4o.inside.btree.BTreeRangeSingle current = (com.db4o.inside.btree.BTreeRangeSingle
					)ranges.Current;
				if (single.Overlaps(current))
				{
					collection.Add(single.Intersect(current));
				}
			}
		}

		public static com.db4o.inside.btree.BTreeRange Intersect(com.db4o.inside.btree.BTreeRangeSingle
			 single1, com.db4o.inside.btree.BTreeRangeSingle single2)
		{
			com.db4o.inside.btree.BTreePointer first = com.db4o.inside.btree.BTreePointer.Max
				(single1.First(), single2.First());
			com.db4o.inside.btree.BTreePointer end = com.db4o.inside.btree.BTreePointer.Min(single1
				.End(), single2.End());
			return single1.NewBTreeRangeSingle(first, end);
		}

		public static com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRangeUnion
			 union1, com.db4o.inside.btree.BTreeRangeUnion union2)
		{
			System.Collections.IEnumerator ranges = union1.Ranges();
			com.db4o.inside.btree.BTreeRange merged = union2;
			while (ranges.MoveNext())
			{
				merged = merged.Union((com.db4o.inside.btree.BTreeRange)ranges.Current);
			}
			return merged;
		}

		public static com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRangeUnion
			 union, com.db4o.inside.btree.BTreeRangeSingle single)
		{
			if (single.IsEmpty())
			{
				return union;
			}
			com.db4o.foundation.SortedCollection4 sorted = NewBTreeRangeSingleCollection();
			sorted.Add(single);
			com.db4o.inside.btree.BTreeRangeSingle range = single;
			System.Collections.IEnumerator ranges = union.Ranges();
			while (ranges.MoveNext())
			{
				com.db4o.inside.btree.BTreeRangeSingle current = (com.db4o.inside.btree.BTreeRangeSingle
					)ranges.Current;
				if (CanBeMerged(current, range))
				{
					sorted.Remove(range);
					range = Merge(current, range);
					sorted.Add(range);
				}
				else
				{
					sorted.Add(current);
				}
			}
			return ToRange(sorted);
		}

		private static com.db4o.inside.btree.BTreeRange ToRange(com.db4o.foundation.SortedCollection4
			 sorted)
		{
			if (1 == sorted.Size())
			{
				return (com.db4o.inside.btree.BTreeRange)sorted.SingleElement();
			}
			return new com.db4o.inside.btree.BTreeRangeUnion(sorted);
		}

		private static com.db4o.foundation.SortedCollection4 NewBTreeRangeSingleCollection
			()
		{
			return new com.db4o.foundation.SortedCollection4(com.db4o.inside.btree.BTreeRangeSingle
				.COMPARISON);
		}

		public static com.db4o.inside.btree.BTreeRange Union(com.db4o.inside.btree.BTreeRangeSingle
			 single1, com.db4o.inside.btree.BTreeRangeSingle single2)
		{
			if (single1.IsEmpty())
			{
				return single2;
			}
			if (single2.IsEmpty())
			{
				return single1;
			}
			if (CanBeMerged(single1, single2))
			{
				return Merge(single1, single2);
			}
			return new com.db4o.inside.btree.BTreeRangeUnion(new com.db4o.inside.btree.BTreeRangeSingle
				[] { single1, single2 });
		}

		private static com.db4o.inside.btree.BTreeRangeSingle Merge(com.db4o.inside.btree.BTreeRangeSingle
			 range1, com.db4o.inside.btree.BTreeRangeSingle range2)
		{
			return range1.NewBTreeRangeSingle(com.db4o.inside.btree.BTreePointer.Min(range1.First
				(), range2.First()), com.db4o.inside.btree.BTreePointer.Max(range1.End(), range2
				.End()));
		}

		private static bool CanBeMerged(com.db4o.inside.btree.BTreeRangeSingle range1, com.db4o.inside.btree.BTreeRangeSingle
			 range2)
		{
			return range1.Overlaps(range2) || range1.Adjacent(range2);
		}
	}
}
