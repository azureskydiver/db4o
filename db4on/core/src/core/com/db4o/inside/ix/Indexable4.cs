namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public interface Indexable4 : com.db4o.YapComparable
	{
		object ComparableObject(com.db4o.Transaction trans, object indexEntry);

		int LinkLength();

		object ReadIndexEntry(com.db4o.YapReader a_reader);

		void WriteIndexEntry(com.db4o.YapReader a_writer, object a_object);

		void DefragIndexEntry(com.db4o.ReaderPair readers);
	}
}
