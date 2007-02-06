namespace com.db4o.@internal.query.result
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
	}
}
