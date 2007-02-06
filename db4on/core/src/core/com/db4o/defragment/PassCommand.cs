namespace com.db4o.defragment
{
	/// <summary>Implements one step in the defragmenting process.</summary>
	/// <remarks>Implements one step in the defragmenting process.</remarks>
	/// <exclude></exclude>
	internal interface PassCommand
	{
		void ProcessObjectSlot(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int id, bool registerAddresses);

		void ProcessClass(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int id, int classIndexID);

		void ProcessClassCollection(com.db4o.defragment.DefragContextImpl context);

		void ProcessBTree(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.btree.BTree
			 btree);

		void Flush(com.db4o.defragment.DefragContextImpl context);
	}
}
