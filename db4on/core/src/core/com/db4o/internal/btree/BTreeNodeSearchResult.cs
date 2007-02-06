namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class BTreeNodeSearchResult
	{
		private readonly com.db4o.@internal.Transaction _transaction;

		private readonly com.db4o.@internal.btree.BTree _btree;

		private readonly com.db4o.@internal.btree.BTreePointer _pointer;

		private readonly bool _foundMatch;

		internal BTreeNodeSearchResult(com.db4o.@internal.Transaction transaction, com.db4o.@internal.btree.BTree
			 btree, com.db4o.@internal.btree.BTreePointer pointer, bool foundMatch)
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

		internal BTreeNodeSearchResult(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 nodeReader, com.db4o.@internal.btree.BTree btree, com.db4o.@internal.btree.BTreeNode
			 node, int cursor, bool foundMatch) : this(trans, btree, PointerOrNull(trans, nodeReader
			, node, cursor), foundMatch)
		{
		}

		internal BTreeNodeSearchResult(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 nodeReader, com.db4o.@internal.btree.BTree btree, com.db4o.@internal.btree.Searcher
			 searcher, com.db4o.@internal.btree.BTreeNode node) : this(trans, btree, NextPointerIf
			(PointerOrNull(trans, nodeReader, node, searcher.Cursor()), searcher.IsGreater()
			), searcher.FoundMatch())
		{
		}

		private static com.db4o.@internal.btree.BTreePointer NextPointerIf(com.db4o.@internal.btree.BTreePointer
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

		private static com.db4o.@internal.btree.BTreePointer PointerOrNull(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer nodeReader, com.db4o.@internal.btree.BTreeNode
			 node, int cursor)
		{
			return node == null ? null : new com.db4o.@internal.btree.BTreePointer(trans, nodeReader
				, node, cursor);
		}

		public virtual com.db4o.@internal.btree.BTreeRange CreateIncludingRange(com.db4o.@internal.btree.BTreeNodeSearchResult
			 end)
		{
			com.db4o.@internal.btree.BTreePointer firstPointer = FirstValidPointer();
			com.db4o.@internal.btree.BTreePointer endPointer = end._foundMatch ? end._pointer
				.Next() : end.FirstValidPointer();
			return new com.db4o.@internal.btree.BTreeRangeSingle(_transaction, _btree, firstPointer
				, endPointer);
		}

		private com.db4o.@internal.btree.BTreePointer FirstValidPointer()
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
