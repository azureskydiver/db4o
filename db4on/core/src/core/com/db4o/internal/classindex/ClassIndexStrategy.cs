namespace com.db4o.@internal.classindex
{
	/// <exclude></exclude>
	public interface ClassIndexStrategy
	{
		void Initialize(com.db4o.@internal.ObjectContainerBase stream);

		void Read(com.db4o.@internal.ObjectContainerBase stream, int indexID);

		int Write(com.db4o.@internal.Transaction transaction);

		void Add(com.db4o.@internal.Transaction transaction, int id);

		void Remove(com.db4o.@internal.Transaction transaction, int id);

		int EntryCount(com.db4o.@internal.Transaction transaction);

		int OwnLength();

		void Purge();

		/// <summary>Traverses all index entries (java.lang.Integer references).</summary>
		/// <remarks>Traverses all index entries (java.lang.Integer references).</remarks>
		void TraverseAll(com.db4o.@internal.Transaction transaction, com.db4o.foundation.Visitor4
			 command);

		void DontDelete(com.db4o.@internal.Transaction transaction, int id);

		System.Collections.IEnumerator AllSlotIDs(com.db4o.@internal.Transaction trans);

		void DefragReference(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.ReaderPair
			 readers, int classIndexID);

		int Id();

		void DefragIndex(com.db4o.@internal.ReaderPair readers);
	}
}
