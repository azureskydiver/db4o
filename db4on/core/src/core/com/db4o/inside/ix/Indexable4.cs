namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public interface Indexable4 : com.db4o.YapComparable
	{
		object comparableObject(com.db4o.Transaction trans, object indexEntry);

		int linkLength();

		object readIndexEntry(com.db4o.YapReader a_reader);

		void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object);
	}
}
