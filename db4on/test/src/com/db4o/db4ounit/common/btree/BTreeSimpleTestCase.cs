namespace com.db4o.db4ounit.common.btree
{
	public class BTreeSimpleTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutDefragSolo
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		protected const int BTREE_NODE_SIZE = 4;

		internal int[] _keys = new int[] { 3, 234, 55, 87, 2, 1, 101, 59, 70, 300, 288 };

		internal int[] _values;

		internal int[] _sortedKeys = new int[] { 1, 2, 3, 55, 59, 70, 87, 101, 234, 288, 
			300 };

		internal int[] _sortedValues;

		internal int[] _keysOnRemoval = new int[] { 1, 2, 55, 59, 70, 87, 234, 288, 300 };

		internal int[] _valuesOnRemoval;

		internal int[] _one = new int[] { 1 };

		internal int[] _none = new int[] {  };

		public BTreeSimpleTestCase() : base()
		{
			_values = new int[_keys.Length];
			for (int i = 0; i < _keys.Length; i++)
			{
				_values[i] = _keys[i];
			}
			_sortedValues = new int[_sortedKeys.Length];
			for (int i = 0; i < _sortedKeys.Length; i++)
			{
				_sortedValues[i] = _sortedKeys[i];
			}
			_valuesOnRemoval = new int[_keysOnRemoval.Length];
			for (int i = 0; i < _keysOnRemoval.Length; i++)
			{
				_valuesOnRemoval[i] = _keysOnRemoval[i];
			}
		}

		public virtual void TestIntKeys()
		{
			com.db4o.@internal.btree.BTree btree = com.db4o.db4ounit.common.btree.BTreeAssert
				.CreateIntKeyBTree(Stream(), 0, BTREE_NODE_SIZE);
			for (int i = 0; i < 5; i++)
			{
				btree = CycleIntKeys(btree);
			}
		}

		private com.db4o.@internal.btree.BTree CycleIntKeys(com.db4o.@internal.btree.BTree
			 btree)
		{
			AddKeys(btree);
			ExpectKeys(btree, _sortedKeys);
			btree.Commit(Trans());
			ExpectKeys(btree, _sortedKeys);
			RemoveKeys(btree);
			ExpectKeys(btree, _keysOnRemoval);
			btree.Rollback(Trans());
			ExpectKeys(btree, _sortedKeys);
			int id = btree.GetID();
			Reopen();
			btree = com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree(Stream(), id
				, BTREE_NODE_SIZE);
			ExpectKeys(btree, _sortedKeys);
			RemoveKeys(btree);
			ExpectKeys(btree, _keysOnRemoval);
			btree.Commit(Trans());
			ExpectKeys(btree, _keysOnRemoval);
			for (int i = 1; i < _keysOnRemoval.Length; i++)
			{
				btree.Remove(Trans(), _keysOnRemoval[i]);
			}
			ExpectKeys(btree, _one);
			btree.Commit(Trans());
			ExpectKeys(btree, _one);
			btree.Remove(Trans(), 1);
			btree.Rollback(Trans());
			ExpectKeys(btree, _one);
			btree.Remove(Trans(), 1);
			btree.Commit(Trans());
			ExpectKeys(btree, _none);
			return btree;
		}

		private void RemoveKeys(com.db4o.@internal.btree.BTree btree)
		{
			btree.Remove(Trans(), 3);
			btree.Remove(Trans(), 101);
		}

		private void AddKeys(com.db4o.@internal.btree.BTree btree)
		{
			com.db4o.@internal.Transaction trans = Trans();
			for (int i = 0; i < _keys.Length; i++)
			{
				btree.Add(trans, _keys[i]);
			}
		}

		private void ExpectKeys(com.db4o.@internal.btree.BTree btree, int[] keys)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertKeys(Trans(), btree, keys);
		}
	}
}
