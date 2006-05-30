namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public interface QueryResult
	{
		object Get(int index);

		long[] GetIDs();

		bool HasNext();

		object Next();

		void Reset();

		int Size();

		object StreamLock();

		com.db4o.ObjectContainer ObjectContainer();

		int IndexOf(int id);

		void Sort(com.db4o.query.QueryComparator cmp);
	}
}
