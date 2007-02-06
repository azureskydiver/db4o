namespace com.db4o.@internal.mapping
{
	/// <summary>Encapsulates services involving source and target database files during defragmenting.
	/// 	</summary>
	/// <remarks>Encapsulates services involving source and target database files during defragmenting.
	/// 	</remarks>
	/// <exclude></exclude>
	public interface DefragContext : com.db4o.@internal.mapping.IDMapping
	{
		com.db4o.@internal.Buffer SourceReaderByAddress(int address, int length);

		com.db4o.@internal.Buffer TargetReaderByAddress(int address, int length);

		com.db4o.@internal.Buffer SourceReaderByID(int sourceID);

		int AllocateTargetSlot(int targetLength);

		void TargetWriteBytes(com.db4o.@internal.Buffer targetPointerReader, int targetID
			);

		com.db4o.@internal.Transaction SystemTrans();

		void TargetWriteBytes(com.db4o.@internal.ReaderPair readers, int targetAddress);

		void TraverseAllIndexSlots(com.db4o.@internal.btree.BTree tree, com.db4o.foundation.Visitor4
			 visitor4);

		com.db4o.@internal.ClassMetadata YapClass(int id);

		com.db4o.@internal.StatefulBuffer SourceWriterByID(int sourceID);

		int MappedID(int id, bool lenient);

		void RegisterUnindexed(int id);

		System.Collections.IEnumerator UnindexedIDs();
	}
}
