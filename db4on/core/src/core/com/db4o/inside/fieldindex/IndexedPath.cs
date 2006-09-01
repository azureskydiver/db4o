namespace com.db4o.inside.fieldindex
{
	public class IndexedPath : com.db4o.inside.fieldindex.IndexedNodeBase
	{
		public static com.db4o.inside.fieldindex.IndexedNode NewParentPath(com.db4o.inside.fieldindex.IndexedNode
			 next, com.db4o.QConObject constraint)
		{
			com.db4o.QCon parent = constraint.Parent();
			if (parent is com.db4o.QConObject)
			{
				return new com.db4o.inside.fieldindex.IndexedPath((com.db4o.QConObject)parent, next
					);
			}
			return null;
		}

		private com.db4o.inside.fieldindex.IndexedNode _next;

		public IndexedPath(com.db4o.QConObject parent, com.db4o.inside.fieldindex.IndexedNode
			 next) : base(parent)
		{
			_next = next;
		}

		public override com.db4o.TreeInt ToTreeInt()
		{
			com.db4o.TreeInt tree = null;
			com.db4o.foundation.KeyValueIterator iterator = Iterator();
			while (iterator.MoveNext())
			{
				com.db4o.inside.btree.FieldIndexKey key = (com.db4o.inside.btree.FieldIndexKey)iterator
					.Key();
				tree = (com.db4o.TreeInt)com.db4o.Tree.Add(tree, new com.db4o.TreeInt(key.ParentID
					()));
			}
			return tree;
		}

		public override com.db4o.foundation.KeyValueIterator Iterator()
		{
			return new com.db4o.inside.fieldindex.IndexedPathIterator(this, _next.Iterator());
		}

		public override int ResultSize()
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return 0;
		}
	}
}
