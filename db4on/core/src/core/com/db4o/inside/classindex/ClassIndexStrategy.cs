namespace com.db4o.inside.classindex
{
	/// <exclude></exclude>
	public interface ClassIndexStrategy
	{
		void Initialize(com.db4o.YapStream stream);

		void Read(com.db4o.YapStream stream, int indexID);

		int Write(com.db4o.Transaction transaction);

		void Add(com.db4o.Transaction transaction, int id);

		void Remove(com.db4o.Transaction transaction, int id);

		int EntryCount(com.db4o.Transaction transaction);

		int OwnLength();

		void Purge();

		/// <summary>Traverses all index entries (java.lang.Integer references).</summary>
		/// <remarks>Traverses all index entries (java.lang.Integer references).</remarks>
		void TraverseAll(com.db4o.Transaction transaction, com.db4o.foundation.Visitor4 command
			);

		void DontDelete(com.db4o.Transaction transaction, int id);

		System.Collections.IEnumerator AllSlotIDs(com.db4o.Transaction trans);

		void DefragReference(com.db4o.YapClass yapClass, com.db4o.ReaderPair readers, int
			 classIndexID);

		int Id();

		void DefragIndex(com.db4o.ReaderPair readers);
	}
}
