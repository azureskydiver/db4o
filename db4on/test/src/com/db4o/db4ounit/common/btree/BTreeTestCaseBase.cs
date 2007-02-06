namespace com.db4o.db4ounit.common.btree
{
	public abstract class BTreeTestCaseBase : Db4oUnit.Extensions.AbstractDb4oTestCase
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		protected const int BTREE_NODE_SIZE = 4;

		protected com.db4o.@internal.btree.BTree _btree;

		protected override void Db4oSetupAfterStore()
		{
			_btree = NewBTree();
		}

		protected virtual com.db4o.@internal.btree.BTree NewBTree()
		{
			return com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree(Stream(), 0, 
				BTREE_NODE_SIZE);
		}

		protected virtual com.db4o.@internal.btree.BTreeRange Range(int lower, int upper)
		{
			com.db4o.@internal.btree.BTreeRange lowerRange = Search(lower);
			com.db4o.@internal.btree.BTreeRange upperRange = Search(upper);
			return lowerRange.ExtendToLastOf(upperRange);
		}

		protected virtual com.db4o.@internal.btree.BTreeRange Search(int key)
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

		protected virtual com.db4o.@internal.btree.BTreeRange Search(com.db4o.@internal.Transaction
			 trans, int key)
		{
			return _btree.Search(trans, key);
		}

		protected virtual void Commit(com.db4o.@internal.Transaction trans)
		{
			_btree.Commit(trans);
		}

		protected virtual void Commit()
		{
			Commit(Trans());
		}

		protected virtual void Remove(com.db4o.@internal.Transaction transaction, int[] keys
			)
		{
			for (int i = 0; i < keys.Length; i++)
			{
				Remove(transaction, keys[i]);
			}
		}

		protected virtual void Add(com.db4o.@internal.Transaction transaction, int[] keys
			)
		{
			for (int i = 0; i < keys.Length; i++)
			{
				Add(transaction, keys[i]);
			}
		}

		protected virtual void AssertEmpty(com.db4o.@internal.Transaction transaction)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertEmpty(transaction, _btree);
		}

		protected virtual void Add(com.db4o.@internal.Transaction transaction, int element
			)
		{
			_btree.Add(transaction, element);
		}

		protected virtual void Remove(int element)
		{
			Remove(Trans(), element);
		}

		protected virtual void Remove(com.db4o.@internal.Transaction trans, int element)
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

		protected virtual void AssertSingleElement(com.db4o.@internal.Transaction trans, 
			int element)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertSingleElement(trans, _btree, element
				);
		}

		protected virtual void AssertPointerKey(int key, com.db4o.@internal.btree.BTreePointer
			 pointer)
		{
			Db4oUnit.Assert.AreEqual(key, pointer.Key());
		}
	}
}
