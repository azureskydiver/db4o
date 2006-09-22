namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTreeNodeSearchResult
	{
		private readonly com.db4o.Transaction _transaction;

		private readonly com.db4o.inside.btree.BTree _btree;

		private readonly com.db4o.inside.btree.BTreePointer _pointer;

		private readonly bool _foundMatch;

		internal BTreeNodeSearchResult(com.db4o.Transaction transaction, com.db4o.inside.btree.BTree
			 btree, com.db4o.inside.btree.BTreePointer pointer, bool foundMatch)
		{
			if (null == transaction || null == btree)
			{
				throw new System.ArgumentNullException();
			}
			_transaction = transaction;
			_btree = btree;
			_pointer = pointer;
			_foundMatch = foundMatch;
		}

		internal BTreeNodeSearchResult(com.db4o.Transaction trans, com.db4o.YapReader nodeReader
			, com.db4o.inside.btree.BTree btree, com.db4o.inside.btree.BTreeNode node, int cursor
			, bool foundMatch) : this(trans, btree, PointerOrNull(trans, nodeReader, node, cursor
			), foundMatch)
		{
		}

		internal BTreeNodeSearchResult(com.db4o.Transaction trans, com.db4o.YapReader nodeReader
			, com.db4o.inside.btree.BTree btree, com.db4o.inside.btree.Searcher searcher, com.db4o.inside.btree.BTreeNode
			 node) : this(trans, btree, NextPointerIf(PointerOrNull(trans, nodeReader, node, 
			searcher.Cursor()), searcher.IsGreater()), searcher.FoundMatch())
		{
		}

		private static com.db4o.inside.btree.BTreePointer NextPointerIf(com.db4o.inside.btree.BTreePointer
			 pointer, bool condition)
		{
			if (null == pointer)
			{
				return null;
			}
			if (condition)
			{
				return pointer.Next();
			}
			return pointer;
		}

		private static com.db4o.inside.btree.BTreePointer PointerOrNull(com.db4o.Transaction
			 trans, com.db4o.YapReader nodeReader, com.db4o.inside.btree.BTreeNode node, int
			 cursor)
		{
			return node == null ? null : new com.db4o.inside.btree.BTreePointer(trans, nodeReader
				, node, cursor);
		}

		public virtual com.db4o.inside.btree.BTreeRange CreateIncludingRange(com.db4o.inside.btree.BTreeNodeSearchResult
			 end)
		{
			com.db4o.inside.btree.BTreePointer firstPointer = FirstValidPointer();
			com.db4o.inside.btree.BTreePointer endPointer = end._foundMatch ? end._pointer.Next
				() : end.FirstValidPointer();
			return new com.db4o.inside.btree.BTreeRangeSingle(_transaction, _btree, firstPointer
				, endPointer);
		}

		private com.db4o.inside.btree.BTreePointer FirstValidPointer()
		{
			if (null == _pointer)
			{
				return null;
			}
			if (_pointer.IsValid())
			{
				return _pointer;
			}
			return _pointer.Next();
		}
	}
}
