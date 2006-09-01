namespace com.db4o.inside.fieldindex
{
	public abstract class IndexedNodeBase : com.db4o.inside.fieldindex.IndexedNode
	{
		private readonly com.db4o.QConObject _constraint;

		public IndexedNodeBase(com.db4o.QConObject qcon)
		{
			if (null == qcon)
			{
				throw new System.ArgumentNullException();
			}
			if (null == qcon.GetField())
			{
				throw new System.ArgumentException();
			}
			_constraint = qcon;
		}

		public virtual com.db4o.inside.btree.BTree GetIndex()
		{
			return GetYapField().GetIndex();
		}

		private com.db4o.YapField GetYapField()
		{
			return _constraint.GetField().GetYapField();
		}

		public virtual com.db4o.QConObject Constraint()
		{
			return _constraint;
		}

		public virtual bool IsResolved()
		{
			com.db4o.QCon parent = Constraint().Parent();
			return null == parent || !parent.HasParent();
		}

		private com.db4o.inside.btree.BTreeNodeSearchResult SearchBound(int bound, object
			 keyPart)
		{
			return GetIndex().SearchLeaf(Transaction(), new com.db4o.inside.btree.FieldIndexKey
				(bound, keyPart), com.db4o.inside.btree.SearchTarget.LOWEST);
		}

		public virtual com.db4o.inside.btree.BTreeRange Search(object value)
		{
			com.db4o.inside.btree.BTreeNodeSearchResult lowerBound = SearchLowerBound(value);
			com.db4o.inside.btree.BTreeNodeSearchResult upperBound = SearchUpperBound(value);
			return lowerBound.CreateIncludingRange(upperBound);
		}

		private com.db4o.inside.btree.BTreeNodeSearchResult SearchUpperBound(object value
			)
		{
			return SearchBound(int.MaxValue, value);
		}

		private com.db4o.inside.btree.BTreeNodeSearchResult SearchLowerBound(object value
			)
		{
			return SearchBound(0, value);
		}

		public static com.db4o.TreeInt AddRangeToTree(com.db4o.TreeInt tree, com.db4o.inside.btree.BTreeRange
			 range)
		{
			com.db4o.foundation.KeyValueIterator i = range.Iterator();
			while (i.MoveNext())
			{
				com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
					)i.Key();
				tree = (com.db4o.TreeInt)com.db4o.Tree.Add(tree, new com.db4o.TreeInt(composite.ParentID
					()));
			}
			return tree;
		}

		public virtual com.db4o.inside.fieldindex.IndexedNode Resolve()
		{
			if (IsResolved())
			{
				return null;
			}
			return com.db4o.inside.fieldindex.IndexedPath.NewParentPath(this, Constraint());
		}

		private com.db4o.Transaction Transaction()
		{
			return Constraint().Transaction();
		}

		public abstract com.db4o.foundation.KeyValueIterator Iterator();

		public abstract int ResultSize();

		public abstract com.db4o.TreeInt ToTreeInt();
	}
}
