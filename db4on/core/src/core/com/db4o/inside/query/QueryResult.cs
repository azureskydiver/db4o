namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public interface QueryResult : System.Collections.IEnumerable
	{
		object Get(int index);

		com.db4o.foundation.IntIterator4 IterateIDs();

		int Size();

		com.db4o.ext.ExtObjectContainer ObjectContainer();

		int IndexOf(int id);

		void Sort(com.db4o.query.QueryComparator cmp);

		void LoadFromClassIndex(com.db4o.YapClass clazz);

		void LoadFromQuery(com.db4o.QQuery query);

		void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator iterator);

		void LoadFromIdReader(com.db4o.YapReader reader);
	}
}
