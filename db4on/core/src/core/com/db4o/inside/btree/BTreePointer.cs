namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreePointer
	{
		public static com.db4o.inside.btree.BTreePointer Max(com.db4o.inside.btree.BTreePointer
			 x, com.db4o.inside.btree.BTreePointer y)
		{
			if (x == null)
			{
				return x;
			}
			if (y == null)
			{
				return y;
			}
			if (x.CompareTo(y) > 0)
			{
				return x;
			}
			return y;
		}

		public static com.db4o.inside.btree.BTreePointer Min(com.db4o.inside.btree.BTreePointer
			 x, com.db4o.inside.btree.BTreePointer y)
		{
			if (x == null)
			{
				return y;
			}
			if (y == null)
			{
				return x;
			}
			if (x.CompareTo(y) < 0)
			{
				return x;
			}
			return y;
		}

		private readonly com.db4o.inside.btree.BTreeNode _node;

		private readonly int _index;

		private readonly com.db4o.Transaction _transaction;

		public BTreePointer(com.db4o.Transaction transaction, com.db4o.inside.btree.BTreeNode
			 node, int index)
		{
			if (transaction == null || node == null)
			{
				throw new System.ArgumentNullException();
			}
			_transaction = transaction;
			_node = node;
			_index = index;
		}

		public virtual com.db4o.Transaction Transaction()
		{
			return _transaction;
		}

		public virtual int Index()
		{
			return _index;
		}

		public virtual com.db4o.inside.btree.BTreePointer Next()
		{
			int indexInMyNode = _index + 1;
			while (indexInMyNode < _node.Count())
			{
				if (_node.IndexIsValid(_transaction, indexInMyNode))
				{
					return new com.db4o.inside.btree.BTreePointer(_transaction, _node, indexInMyNode);
				}
				indexInMyNode++;
			}
			int newIndex = -1;
			com.db4o.inside.btree.BTreeNode nextNode = _node;
			while (newIndex == -1)
			{
				nextNode = nextNode.NextNode();
				if (nextNode == null)
				{
					return null;
				}
				nextNode.PrepareWrite(_transaction);
				newIndex = nextNode.FirstKeyIndex(_transaction);
			}
			return new com.db4o.inside.btree.BTreePointer(_transaction, nextNode, newIndex);
		}

		public virtual com.db4o.inside.btree.BTreeNode Node()
		{
			return _node;
		}

		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (!(obj is com.db4o.inside.btree.BTreePointer))
			{
				return false;
			}
			com.db4o.inside.btree.BTreePointer other = (com.db4o.inside.btree.BTreePointer)obj;
			if (_index != other._index)
			{
				return false;
			}
			return _node.Equals(other._node);
		}

		internal virtual object Key()
		{
			Node().PrepareWrite(_transaction);
			return Node().Key(_transaction, Index());
		}

		internal virtual object Value()
		{
			return Node().Value(Index());
		}

		public override string ToString()
		{
			string key = "[Unavail]";
			try
			{
				key = Key().ToString();
			}
			catch (System.Exception e)
			{
			}
			return "BTreePointer (" + _index + ") to " + key + " on" + Node().ToString();
		}

		public virtual int CompareTo(com.db4o.inside.btree.BTreePointer y)
		{
			if (null == y)
			{
				throw new System.ArgumentNullException();
			}
			if (Btree() != y.Btree())
			{
				throw new System.ArgumentException();
			}
			return Btree().CompareKeys(Key(), y.Key());
		}

		private com.db4o.inside.btree.BTree Btree()
		{
			return _node.Btree();
		}

		public static bool LessThan(com.db4o.inside.btree.BTreePointer x, com.db4o.inside.btree.BTreePointer
			 y)
		{
			return com.db4o.inside.btree.BTreePointer.Min(x, y) == x && !Equals(x, y);
		}

		public static bool Equals(com.db4o.inside.btree.BTreePointer x, com.db4o.inside.btree.BTreePointer
			 y)
		{
			if (x == null)
			{
				return y == null;
			}
			return x.Equals(y);
		}
	}
}
