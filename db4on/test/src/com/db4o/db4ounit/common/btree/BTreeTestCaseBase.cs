namespace com.db4o.db4ounit.common.btree
{
	public abstract class BTreeTestCaseBase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		protected com.db4o.inside.btree.BTree _btree;

		public override void SetUp()
		{
			base.SetUp();
			_btree = NewBTree();
		}

		protected virtual com.db4o.inside.btree.BTree NewBTree()
		{
			return com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree(Stream(), 0);
		}

		protected virtual com.db4o.inside.btree.BTreeRange Range(int lower, int upper)
		{
			com.db4o.inside.btree.BTreeRange lowerRange = Search(lower);
			com.db4o.inside.btree.BTreeRange upperRange = Search(upper);
			return lowerRange.ExtendToLastOf(upperRange);
		}

		protected virtual com.db4o.inside.btree.BTreeRange Search(int key)
		{
			return Search(Trans(), key);
		}

		protected virtual void Add(int[] keys)
		{
			for (int i = 0; i < keys.Length; ++i)
			{
				Add(keys[i]);
			}
		}

		protected virtual com.db4o.inside.btree.BTreeRange Search(com.db4o.Transaction trans
			, int key)
		{
			return _btree.Search(trans, key);
		}

		protected virtual void Commit(com.db4o.Transaction trans)
		{
			_btree.Commit(trans);
		}

		protected virtual void Commit()
		{
			Commit(Trans());
		}

		protected virtual void Remove(com.db4o.Transaction transaction, int[] keys)
		{
			for (int i = 0; i < keys.Length; i++)
			{
				Remove(transaction, keys[i]);
			}
		}

		protected virtual void Add(com.db4o.Transaction transaction, int[] keys)
		{
			for (int i = 0; i < keys.Length; i++)
			{
				Add(transaction, keys[i]);
			}
		}

		protected virtual void AssertEmpty(com.db4o.Transaction transaction)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertEmpty(transaction, _btree);
		}

		protected virtual void Add(com.db4o.Transaction transaction, int element)
		{
			_btree.Add(transaction, element);
		}

		protected virtual void Remove(int element)
		{
			Remove(Trans(), element);
		}

		protected virtual void Remove(com.db4o.Transaction trans, int element)
		{
			_btree.Remove(trans, element);
		}

		protected virtual void Add(int element)
		{
			Add(Trans(), element);
		}

		private int Size()
		{
			return _btree.Size(Trans());
		}

		protected virtual void AssertSize(int expected)
		{
			Db4oUnit.Assert.AreEqual(expected, Size());
		}

		protected virtual void AssertSingleElement(int element)
		{
			AssertSingleElement(Trans(), element);
		}

		protected virtual void AssertSingleElement(com.db4o.Transaction trans, int element
			)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertSingleElement(trans, _btree, element
				);
		}
	}
}
