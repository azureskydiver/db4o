namespace com.db4o.db4ounit.common.btree
{
	public class BTreeSimpleTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		internal int[] _keys = { 3, 234, 55, 87, 2, 1, 101, 59, 70, 300, 288 };

		internal int[] _values;

		internal int[] _sortedKeys = { 1, 2, 3, 55, 59, 70, 87, 101, 234, 288, 300 };

		internal int[] _sortedValues;

		internal int[] _keysOnRemoval = { 1, 2, 55, 59, 70, 87, 234, 288, 300 };

		internal int[] _valuesOnRemoval;

		internal int[] _one = { 1 };

		internal int[] _none = {  };

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
			com.db4o.inside.btree.BTree btree = com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree
				(Stream(), 0);
			for (int i = 0; i < 5; i++)
			{
				btree = CycleIntKeys(btree);
			}
		}

		public virtual void TestIntKeysIntValues()
		{
			com.db4o.inside.btree.BTree btree = CreateIntKeyValueBTree(0);
			for (int i = 0; i < 5; i++)
			{
				btree = CycleIntKeysIntValues(btree);
			}
		}

		private com.db4o.inside.btree.BTree CycleIntKeys(com.db4o.inside.btree.BTree btree
			)
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
				);
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

		private com.db4o.inside.btree.BTree CycleIntKeysIntValues(com.db4o.inside.btree.BTree
			 btree)
		{
			AddKeysValues(btree);
			ExpectKeys(btree, _sortedKeys);
			ExpectValues(btree, _sortedValues);
			btree.Commit(Trans());
			ExpectKeys(btree, _sortedKeys);
			ExpectValues(btree, _sortedValues);
			RemoveKeys(btree);
			ExpectKeys(btree, _keysOnRemoval);
			ExpectValues(btree, _valuesOnRemoval);
			btree.Rollback(Trans());
			ExpectKeys(btree, _sortedKeys);
			ExpectValues(btree, _sortedValues);
			int id = btree.GetID();
			Reopen();
			btree = CreateIntKeyValueBTree(id);
			ExpectKeys(btree, _sortedKeys);
			ExpectValues(btree, _sortedValues);
			RemoveKeys(btree);
			ExpectKeys(btree, _keysOnRemoval);
			ExpectValues(btree, _valuesOnRemoval);
			btree.Commit(Trans());
			ExpectKeys(btree, _keysOnRemoval);
			ExpectValues(btree, _valuesOnRemoval);
			for (int i = 1; i < _keysOnRemoval.Length; i++)
			{
				btree.Remove(Trans(), _keysOnRemoval[i]);
			}
			ExpectKeys(btree, _one);
			ExpectValues(btree, _one);
			btree.Commit(Trans());
			ExpectKeys(btree, _one);
			ExpectValues(btree, _one);
			btree.Remove(Trans(), 1);
			btree.Rollback(Trans());
			ExpectKeys(btree, _one);
			ExpectValues(btree, _one);
			btree.Remove(Trans(), 1);
			btree.Commit(Trans());
			ExpectKeys(btree, _none);
			ExpectValues(btree, _none);
			return btree;
		}

		private void RemoveKeys(com.db4o.inside.btree.BTree btree)
		{
			btree.Remove(Trans(), 3);
			btree.Remove(Trans(), 101);
		}

		private void AddKeys(com.db4o.inside.btree.BTree btree)
		{
			com.db4o.Transaction trans = Trans();
			for (int i = 0; i < _keys.Length; i++)
			{
				btree.Add(trans, _keys[i]);
			}
		}

		private void AddKeysValues(com.db4o.inside.btree.BTree btree)
		{
			com.db4o.Transaction trans = Trans();
			for (int i = 0; i < _keys.Length; i++)
			{
				btree.Add(trans, _keys[i], _values[i]);
			}
		}

		private void ExpectValues(com.db4o.inside.btree.BTree btree, int[] values)
		{
			int[] cursor = new int[] { 0 };
			btree.TraverseValues(Trans(), new _AnonymousInnerClass218(this, values, cursor));
			Db4oUnit.Assert.AreEqual(values.Length, cursor[0]);
		}

		private sealed class _AnonymousInnerClass218 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass218(BTreeSimpleTestCase _enclosing, int[] values, int[]
				 cursor)
			{
				this._enclosing = _enclosing;
				this.values = values;
				this.cursor = cursor;
			}

			public void Visit(object obj)
			{
				Db4oUnit.Assert.AreEqual(values[cursor[0]], ((int)obj));
				cursor[0]++;
			}

			private readonly BTreeSimpleTestCase _enclosing;

			private readonly int[] values;

			private readonly int[] cursor;
		}

		private com.db4o.inside.btree.BTree CreateIntKeyValueBTree(int id)
		{
			return new com.db4o.inside.btree.BTree(Stream().GetSystemTransaction(), id, new com.db4o.YInt
				(Stream()), new com.db4o.YInt(Stream()), 7, Stream().ConfigImpl().BTreeCacheHeight
				());
		}

		private void ExpectKeys(com.db4o.inside.btree.BTree btree, int[] keys)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertKeys(Trans(), btree, keys);
		}
	}
}
