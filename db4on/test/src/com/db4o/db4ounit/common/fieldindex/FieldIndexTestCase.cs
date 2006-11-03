namespace com.db4o.db4ounit.common.fieldindex
{
	public class FieldIndexTestCase : com.db4o.db4ounit.common.fieldindex.FieldIndexTestCaseBase
	{
		private static readonly int[] FOOS = new int[] { 3, 7, 9, 4 };

		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.fieldindex.FieldIndexTestCase().RunSolo();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
		}

		protected override void Store()
		{
			StoreItems(FOOS);
		}

		public virtual void TestTraverseValues()
		{
			com.db4o.ext.StoredField field = YapField();
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(FOOS));
			field.TraverseValues(expectingVisitor);
			expectingVisitor.AssertExpectations();
		}

		public virtual void TestAllThere()
		{
			for (int i = 0; i < FOOS.Length; i++)
			{
				com.db4o.query.Query q = CreateQuery(FOOS[i]);
				com.db4o.ObjectSet objectSet = q.Execute();
				Db4oUnit.Assert.AreEqual(1, objectSet.Size());
				com.db4o.db4ounit.common.fieldindex.FieldIndexItem fii = (com.db4o.db4ounit.common.fieldindex.FieldIndexItem
					)objectSet.Next();
				Db4oUnit.Assert.AreEqual(FOOS[i], fii.foo);
			}
		}

		public virtual void TestAccessingBTree()
		{
			com.db4o.inside.btree.BTree bTree = YapField().GetIndex(Trans());
			Db4oUnit.Assert.IsNotNull(bTree);
			ExpectKeysSearch(bTree, FOOS);
		}

		private void ExpectKeysSearch(com.db4o.inside.btree.BTree btree, int[] values)
		{
			int lastValue = int.MinValue;
			for (int i = 0; i < values.Length; i++)
			{
				if (values[i] != lastValue)
				{
					com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = com.db4o.db4ounit.common.btree.BTreeAssert
						.CreateExpectingVisitor(values[i], com.db4o.db4ounit.common.foundation.IntArrays4
						.Occurences(values, values[i]));
					com.db4o.inside.btree.BTreeRange range = FieldIndexKeySearch(Trans(), btree, values
						[i]);
					com.db4o.db4ounit.common.btree.BTreeAssert.TraverseKeys(range, new _AnonymousInnerClass63
						(this, expectingVisitor));
					expectingVisitor.AssertExpectations();
					lastValue = values[i];
				}
			}
		}

		private sealed class _AnonymousInnerClass63 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass63(FieldIndexTestCase _enclosing, com.db4o.db4ounit.common.btree.ExpectingVisitor
				 expectingVisitor)
			{
				this._enclosing = _enclosing;
				this.expectingVisitor = expectingVisitor;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.btree.FieldIndexKey fik = (com.db4o.inside.btree.FieldIndexKey)obj;
				expectingVisitor.Visit(fik.Value());
			}

			private readonly FieldIndexTestCase _enclosing;

			private readonly com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor;
		}

		private com.db4o.inside.btree.FieldIndexKey FieldIndexKey(int integerPart, object
			 composite)
		{
			return new com.db4o.inside.btree.FieldIndexKey(integerPart, composite);
		}

		private com.db4o.inside.btree.BTreeRange FieldIndexKeySearch(com.db4o.Transaction
			 trans, com.db4o.inside.btree.BTree btree, object key)
		{
			com.db4o.inside.btree.BTreeNodeSearchResult start = btree.SearchLeaf(trans, FieldIndexKey
				(0, key), com.db4o.inside.btree.SearchTarget.LOWEST);
			com.db4o.inside.btree.BTreeNodeSearchResult end = btree.SearchLeaf(trans, FieldIndexKey
				(int.MaxValue, key), com.db4o.inside.btree.SearchTarget.LOWEST);
			return start.CreateIncludingRange(end);
		}

		private com.db4o.YapField YapField()
		{
			com.db4o.reflect.ReflectClass claxx = Stream().Reflector().ForObject(new com.db4o.db4ounit.common.fieldindex.FieldIndexItem
				());
			com.db4o.YapClass yc = Stream().GetYapClass(claxx, false);
			com.db4o.YapField yf = yc.GetYapField("foo");
			return yf;
		}
	}
}
