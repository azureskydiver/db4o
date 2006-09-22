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

		public virtual com.db4o.TreeInt ToTreeInt()
		{
			return AddToTree(null, this);
		}

		public com.db4o.inside.btree.BTree GetIndex()
		{
			return GetYapField().GetIndex(Transaction());
		}

		private com.db4o.YapField GetYapField()
		{
			return _constraint.GetField().GetYapField();
		}

		public virtual com.db4o.QCon Constraint()
		{
			return _constraint;
		}

		public virtual bool IsResolved()
		{
			com.db4o.QCon parent = Constraint().Parent();
			return null == parent || !parent.HasParent();
		}

		public virtual com.db4o.inside.btree.BTreeRange Search(object value)
		{
			return GetYapField().Search(Transaction(), value);
		}

		public static com.db4o.TreeInt AddToTree(com.db4o.TreeInt tree, com.db4o.inside.fieldindex.IndexedNode
			 node)
		{
			com.db4o.foundation.Iterator4 i = node.Iterator();
			while (i.MoveNext())
			{
				com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
					)i.Current();
				tree = (com.db4o.TreeInt)com.db4o.foundation.Tree.Add(tree, new com.db4o.TreeInt(
					composite.ParentID()));
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

		public abstract com.db4o.foundation.Iterator4 Iterator();

		public abstract int ResultSize();
	}
}
