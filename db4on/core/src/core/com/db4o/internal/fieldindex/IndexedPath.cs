namespace com.db4o.@internal.fieldindex
{
	public class IndexedPath : com.db4o.@internal.fieldindex.IndexedNodeBase
	{
		public static com.db4o.@internal.fieldindex.IndexedNode NewParentPath(com.db4o.@internal.fieldindex.IndexedNode
			 next, com.db4o.@internal.query.processor.QCon constraint)
		{
			if (!CanFollowParent(constraint))
			{
				return null;
			}
			return new com.db4o.@internal.fieldindex.IndexedPath((com.db4o.@internal.query.processor.QConObject
				)constraint.Parent(), next);
		}

		private static bool CanFollowParent(com.db4o.@internal.query.processor.QCon con)
		{
			com.db4o.@internal.query.processor.QCon parent = con.Parent();
			com.db4o.@internal.FieldMetadata parentField = GetYapField(parent);
			if (null == parentField)
			{
				return false;
			}
			com.db4o.@internal.FieldMetadata conField = GetYapField(con);
			if (null == conField)
			{
				return false;
			}
			return parentField.HasIndex() && parentField.GetParentYapClass().IsAssignableFrom
				(conField.GetParentYapClass());
		}

		private static com.db4o.@internal.FieldMetadata GetYapField(com.db4o.@internal.query.processor.QCon
			 con)
		{
			com.db4o.@internal.query.processor.QField field = con.GetField();
			if (null == field)
			{
				return null;
			}
			return field.GetYapField();
		}

		private com.db4o.@internal.fieldindex.IndexedNode _next;

		public IndexedPath(com.db4o.@internal.query.processor.QConObject parent, com.db4o.@internal.fieldindex.IndexedNode
			 next) : base(parent)
		{
			_next = next;
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return new com.db4o.@internal.fieldindex.IndexedPathIterator(this, _next.GetEnumerator
				());
		}

		public override int ResultSize()
		{
			throw new System.NotSupportedException();
		}
	}
}
