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

		internal BTreeNodeSearchResult(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree, com.db4o.inside.btree.BTreeNode node, int cursor, bool foundMatch) : this
			(trans, btree, PointerOrNull(trans, node, cursor), foundMatch)
		{
		}

		internal BTreeNodeSearchResult(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree, com.db4o.inside.btree.Searcher searcher, com.db4o.inside.btree.BTreeNode
			 node) : this(trans, btree, NextPointerIf(PointerOrNull(trans, node, searcher.Cursor
			()), searcher.IsGreater()), searcher.FoundMatch())
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
			 trans, com.db4o.inside.btree.BTreeNode node, int cursor)
		{
			return node == null ? null : new com.db4o.inside.btree.BTreePointer(trans, node, 
				cursor);
		}

		public virtual com.db4o.inside.btree.BTreeRange CreateIncludingRange(com.db4o.inside.btree.BTreeNodeSearchResult
			 end)
		{
			com.db4o.inside.btree.BTreePointer endPointer = end._pointer;
			if (endPointer != null && end._foundMatch)
			{
				endPointer = endPointer.Next();
			}
			return new com.db4o.inside.btree.BTreeRangeImpl(_transaction, _btree, _pointer, endPointer
				);
		}
	}
}
