namespace com.db4o.inside.mapping
{
	/// <summary>Encapsulates services involving source and target database files during defragmenting.
	/// 	</summary>
	/// <remarks>Encapsulates services involving source and target database files during defragmenting.
	/// 	</remarks>
	/// <exclude></exclude>
	public interface DefragContext : com.db4o.inside.mapping.IDMapping
	{
		com.db4o.YapReader SourceReaderByAddress(int address, int length);

		com.db4o.YapReader TargetReaderByAddress(int address, int length);

		com.db4o.YapReader SourceReaderByID(int sourceID);

		int AllocateTargetSlot(int targetLength);

		void TargetWriteBytes(com.db4o.YapReader targetPointerReader, int targetID);

		com.db4o.Transaction SystemTrans();

		void TargetWriteBytes(com.db4o.ReaderPair readers, int targetAddress);

		void TraverseAllIndexSlots(com.db4o.inside.btree.BTree tree, com.db4o.foundation.Visitor4
			 visitor4);

		com.db4o.YapClass YapClass(int id);

		com.db4o.YapWriter SourceWriterByID(int sourceID);

		int MappedID(int id, bool lenient);

		void RegisterSeen(int id);
	}
}
