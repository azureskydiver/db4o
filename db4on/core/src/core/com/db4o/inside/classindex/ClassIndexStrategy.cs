namespace com.db4o.inside.classindex
{
	/// <exclude></exclude>
	public interface ClassIndexStrategy
	{
		void Initialize(com.db4o.YapStream stream);

		void Read(com.db4o.YapReader reader, com.db4o.YapStream stream);

		void WriteId(com.db4o.YapReader writer, com.db4o.Transaction transaction);

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

		void TraverseAllSlotIDs(com.db4o.Transaction trans, com.db4o.foundation.Visitor4 
			command);

		void DefragReference(com.db4o.YapClass yapClass, com.db4o.YapReader source, com.db4o.YapReader
			 target, com.db4o.IDMapping mapping, int classIndexID);

		int Id();

		void DefragIndex(com.db4o.YapReader source, com.db4o.YapReader target, com.db4o.IDMapping
			 mapping);
	}
}
