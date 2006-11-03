namespace com.db4o.inside.fieldindex
{
	public class IndexedPath : com.db4o.inside.fieldindex.IndexedNodeBase
	{
		public static com.db4o.inside.fieldindex.IndexedNode NewParentPath(com.db4o.inside.fieldindex.IndexedNode
			 next, com.db4o.QCon constraint)
		{
			if (!CanFollowParent(constraint))
			{
				return null;
			}
			return new com.db4o.inside.fieldindex.IndexedPath((com.db4o.QConObject)constraint
				.Parent(), next);
		}

		private static bool CanFollowParent(com.db4o.QCon con)
		{
			com.db4o.QCon parent = con.Parent();
			com.db4o.YapField parentField = GetYapField(parent);
			if (null == parentField)
			{
				return false;
			}
			com.db4o.YapField conField = GetYapField(con);
			if (null == conField)
			{
				return false;
			}
			return parentField.HasIndex() && parentField.GetParentYapClass().IsAssignableFrom
				(conField.GetParentYapClass());
		}

		private static com.db4o.YapField GetYapField(com.db4o.QCon con)
		{
			com.db4o.QField field = con.GetField();
			if (null == field)
			{
				return null;
			}
			return field.GetYapField();
		}

		private com.db4o.inside.fieldindex.IndexedNode _next;

		public IndexedPath(com.db4o.QConObject parent, com.db4o.inside.fieldindex.IndexedNode
			 next) : base(parent)
		{
			_next = next;
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return new com.db4o.inside.fieldindex.IndexedPathIterator(this, _next.GetEnumerator
				());
		}

		public override int ResultSize()
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return 0;
		}
	}
}
