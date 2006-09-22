namespace com.db4o.inside.fieldindex
{
	internal sealed class IndexedPathIterator : com.db4o.foundation.CompositeIterator4
	{
		private com.db4o.inside.fieldindex.IndexedPath _path;

		public IndexedPathIterator(com.db4o.inside.fieldindex.IndexedPath path, com.db4o.foundation.Iterator4
			 iterator) : base(iterator)
		{
			_path = path;
		}

		protected override com.db4o.foundation.Iterator4 NextIterator(object current)
		{
			com.db4o.inside.btree.FieldIndexKey key = (com.db4o.inside.btree.FieldIndexKey)current;
			return _path.Search(key.ParentID()).Keys();
		}
	}
}
