namespace com.db4o.@internal.ix
{
	/// <exclude></exclude>
	public interface Indexable4 : com.db4o.@internal.Comparable4
	{
		object ComparableObject(com.db4o.@internal.Transaction trans, object indexEntry);

		int LinkLength();

		object ReadIndexEntry(com.db4o.@internal.Buffer a_reader);

		void WriteIndexEntry(com.db4o.@internal.Buffer a_writer, object a_object);

		void DefragIndexEntry(com.db4o.@internal.ReaderPair readers);
	}
}
