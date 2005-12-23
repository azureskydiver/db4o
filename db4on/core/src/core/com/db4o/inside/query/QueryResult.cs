namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public interface QueryResult
	{
		object get(int index);

		long[] getIDs();

		bool hasNext();

		object next();

		void reset();

		int size();

		object streamLock();

		com.db4o.ObjectContainer objectContainer();

		int indexOf(int id);

		void sort(com.db4o.query.QueryComparator cmp);
	}
}
