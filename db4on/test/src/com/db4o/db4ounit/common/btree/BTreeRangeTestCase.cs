namespace com.db4o.db4ounit.common.btree
{
	public class BTreeRangeTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreeRangeTestCase().RunSolo();
		}

		protected override void Db4oSetupAfterStore()
		{
			base.Db4oSetupAfterStore();
			Add(new int[] { 3, 7, 4, 9 });
		}

		public virtual void TestLastPointer()
		{
			AssertLastPointer(8, 7);
			AssertLastPointer(11, 9);
			AssertLastPointer(4, 3);
		}

		private void AssertLastPointer(int searchValue, int expectedValue)
		{
			com.db4o.inside.btree.BTreeRange single = Search(searchValue);
			com.db4o.inside.btree.BTreeRange smallerRange = single.Smaller();
			com.db4o.inside.btree.BTreePointer lastPointer = smallerRange.LastPointer();
			Db4oUnit.Assert.AreEqual(expectedValue, lastPointer.Key());
		}

		public virtual void TestSize()
		{
			AssertSize(4, Range(3, 9));
			AssertSize(3, Range(4, 9));
			AssertSize(3, Range(3, 7));
			AssertSize(4, Range(2, 9));
			AssertSize(4, Range(3, 10));
			Add(new int[] { 5, 6, 8, 10, 2, 1 });
			AssertSize(10, Range(1, 10));
			AssertSize(9, Range(1, 9));
			AssertSize(9, Range(2, 10));
			AssertSize(9, Range(2, 11));
			AssertSize(10, Range(0, 10));
		}

		private void AssertSize(int size, com.db4o.inside.btree.BTreeRange range)
		{
			Db4oUnit.Assert.AreEqual(size, range.Size());
		}

		public virtual void TestIntersectSingleSingle()
		{
			AssertIntersection(new int[] { 4, 7 }, Range(3, 7), Range(4, 9));
			AssertIntersection(new int[] {  }, Range(3, 4), Range(7, 9));
			AssertIntersection(new int[] { 3, 4, 7, 9 }, Range(3, 9), Range(3, 9));
			AssertIntersection(new int[] { 3, 4, 7, 9 }, Range(3, 10), Range(3, 9));
			AssertIntersection(new int[] {  }, Range(1, 2), Range(3, 9));
		}

		public virtual void TestIntersectSingleUnion()
		{
			com.db4o.inside.btree.BTreeRange union = Range(3, 3).Union(Range(7, 9));
			com.db4o.inside.btree.BTreeRange single = Range(4, 7);
			AssertIntersection(new int[] { 7 }, union, single);
			AssertIntersection(new int[] { 3, 7 }, union, Range(3, 7));
		}

		public virtual void TestIntersectUnionUnion()
		{
			com.db4o.inside.btree.BTreeRange union1 = Range(3, 3).Union(Range(7, 9));
			com.db4o.inside.btree.BTreeRange union2 = Range(3, 3).Union(Range(9, 9));
			AssertIntersection(new int[] { 3, 9 }, union1, union2);
		}

		public virtual void TestUnion()
		{
			AssertUnion(new int[] { 3, 4, 7, 9 }, Range(3, 4), Range(7, 9));
			AssertUnion(new int[] { 3, 4, 7, 9 }, Range(3, 7), Range(4, 9));
			AssertUnion(new int[] { 3, 7, 9 }, Range(3, 3), Range(7, 9));
			AssertUnion(new int[] { 3, 9 }, Range(3, 3), Range(9, 9));
		}

		public virtual void TestIsEmpty()
		{
			Db4oUnit.Assert.IsTrue(Range(0, 0).IsEmpty());
			Db4oUnit.Assert.IsFalse(Range(3, 3).IsEmpty());
			Db4oUnit.Assert.IsFalse(Range(9, 9).IsEmpty());
			Db4oUnit.Assert.IsTrue(Range(10, 10).IsEmpty());
		}

		public virtual void TestUnionWithEmptyDoesNotCreateNewRange()
		{
			com.db4o.inside.btree.BTreeRange range = Range(3, 4);
			com.db4o.inside.btree.BTreeRange empty = Range(0, 0);
			Db4oUnit.Assert.AreSame(range, range.Union(empty));
			Db4oUnit.Assert.AreSame(range, empty.Union(range));
			com.db4o.inside.btree.BTreeRange union = range.Union(Range(8, 9));
			Db4oUnit.Assert.AreSame(union, union.Union(empty));
			Db4oUnit.Assert.AreSame(union, empty.Union(union));
		}

		public virtual void TestUnionsMerge()
		{
			com.db4o.inside.btree.BTreeRange range = Range(3, 3).Union(Range(7, 7)).Union(Range
				(4, 4));
			AssertIsRangeSingle(range);
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 3, 4, 7 }, range
				);
		}

		private void AssertIsRangeSingle(com.db4o.inside.btree.BTreeRange range)
		{
			Db4oUnit.Assert.IsInstanceOf(typeof(com.db4o.inside.btree.BTreeRangeSingle), range
				);
		}

		public virtual void TestUnionsOfUnions()
		{
			com.db4o.inside.btree.BTreeRange union1 = Range(3, 4).Union(Range(8, 9));
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 3, 4, 9 }, union1
				);
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 3, 4, 7, 9 }, 
				union1.Union(Range(7, 7)));
			com.db4o.inside.btree.BTreeRange union2 = Range(3, 3).Union(Range(7, 7));
			AssertUnion(new int[] { 3, 4, 7, 9 }, union1, union2);
			AssertIsRangeSingle(union1.Union(union2));
			AssertIsRangeSingle(union2.Union(union1));
			com.db4o.inside.btree.BTreeRange union3 = Range(3, 3).Union(Range(9, 9));
			AssertUnion(new int[] { 3, 7, 9 }, union2, union3);
		}

		public virtual void TestExtendToLastOf()
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 3, 4, 7 }, Range
				(3, 7));
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 4, 7, 9 }, Range
				(4, 9));
		}

		public virtual void TestUnionOfOverlappingSingleRangesYieldSingleRange()
		{
			Db4oUnit.Assert.IsInstanceOf(typeof(com.db4o.inside.btree.BTreeRangeSingle), Range
				(3, 4).Union(Range(4, 9)));
		}

		private void AssertUnion(int[] expectedKeys, com.db4o.inside.btree.BTreeRange range1
			, com.db4o.inside.btree.BTreeRange range2)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(expectedKeys, range1.Union
				(range2));
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(expectedKeys, range2.Union
				(range1));
		}

		private void AssertIntersection(int[] expectedKeys, com.db4o.inside.btree.BTreeRange
			 range1, com.db4o.inside.btree.BTreeRange range2)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(expectedKeys, range1.Intersect
				(range2));
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(expectedKeys, range2.Intersect
				(range1));
		}
	}
}
