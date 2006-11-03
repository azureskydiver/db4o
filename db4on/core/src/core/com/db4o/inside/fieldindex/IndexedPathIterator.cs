namespace com.db4o.inside.fieldindex
{
	internal sealed class IndexedPathIterator : com.db4o.foundation.CompositeIterator4
	{
		private com.db4o.inside.fieldindex.IndexedPath _path;

		public IndexedPathIterator(com.db4o.inside.fieldindex.IndexedPath path, System.Collections.IEnumerator
			 iterator) : base(iterator)
		{
			_path = path;
		}

		protected override System.Collections.IEnumerator NextIterator(object current)
		{
			com.db4o.inside.btree.FieldIndexKey key = (com.db4o.inside.btree.FieldIndexKey)current;
			return _path.Search(key.ParentID()).Keys();
		}
	}
}
