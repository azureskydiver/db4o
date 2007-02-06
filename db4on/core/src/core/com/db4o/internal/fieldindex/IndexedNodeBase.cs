namespace com.db4o.@internal.fieldindex
{
	public abstract class IndexedNodeBase : com.db4o.@internal.fieldindex.IndexedNode
	{
		private readonly com.db4o.@internal.query.processor.QConObject _constraint;

		public IndexedNodeBase(com.db4o.@internal.query.processor.QConObject qcon)
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

		public virtual com.db4o.@internal.TreeInt ToTreeInt()
		{
			return AddToTree(null, this);
		}

		public com.db4o.@internal.btree.BTree GetIndex()
		{
			return GetYapField().GetIndex(Transaction());
		}

		private com.db4o.@internal.FieldMetadata GetYapField()
		{
			return _constraint.GetField().GetYapField();
		}

		public virtual com.db4o.@internal.query.processor.QCon Constraint()
		{
			return _constraint;
		}

		public virtual bool IsResolved()
		{
			com.db4o.@internal.query.processor.QCon parent = Constraint().Parent();
			return null == parent || !parent.HasParent();
		}

		public virtual com.db4o.@internal.btree.BTreeRange Search(object value)
		{
			return GetYapField().Search(Transaction(), value);
		}

		public static com.db4o.@internal.TreeInt AddToTree(com.db4o.@internal.TreeInt tree
			, com.db4o.@internal.fieldindex.IndexedNode node)
		{
			System.Collections.IEnumerator i = node.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.btree.FieldIndexKey composite = (com.db4o.@internal.btree.FieldIndexKey
					)i.Current;
				tree = (com.db4o.@internal.TreeInt)com.db4o.foundation.Tree.Add(tree, new com.db4o.@internal.TreeInt
					(composite.ParentID()));
			}
			return tree;
		}

		public virtual com.db4o.@internal.fieldindex.IndexedNode Resolve()
		{
			if (IsResolved())
			{
				return null;
			}
			return com.db4o.@internal.fieldindex.IndexedPath.NewParentPath(this, Constraint()
				);
		}

		private com.db4o.@internal.Transaction Transaction()
		{
			return Constraint().Transaction();
		}

		public abstract System.Collections.IEnumerator GetEnumerator();

		public abstract int ResultSize();
	}
}
