namespace com.db4o.defragment
{
	/// <summary>
	/// Second step in the defragmenting process: Fills in target file pointer slots, copies
	/// content slots from source to target and triggers ID remapping therein by calling the
	/// appropriate yap/marshaller defrag() implementations.
	/// </summary>
	/// <remarks>
	/// Second step in the defragmenting process: Fills in target file pointer slots, copies
	/// content slots from source to target and triggers ID remapping therein by calling the
	/// appropriate yap/marshaller defrag() implementations. During the process, the actual address
	/// mappings for the content slots are registered for use with string indices.
	/// </remarks>
	/// <exclude></exclude>
	internal sealed class SecondPassCommand : com.db4o.defragment.PassCommand
	{
		private readonly int _objectCommitFrequency;

		private int _objectCount = 0;

		public SecondPassCommand(int objectCommitFrequency)
		{
			_objectCommitFrequency = objectCommitFrequency;
		}

		public void ProcessClass(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int id, int classIndexID)
		{
			if (context.MappedID(id, -1) == -1)
			{
				j4o.lang.JavaSystem.Err.WriteLine("MAPPING NOT FOUND: " + id);
			}
			com.db4o.@internal.ReaderPair.ProcessCopy(context, id, new _AnonymousInnerClass32
				(this, yapClass, classIndexID));
		}

		private sealed class _AnonymousInnerClass32 : com.db4o.@internal.SlotCopyHandler
		{
			public _AnonymousInnerClass32(SecondPassCommand _enclosing, com.db4o.@internal.ClassMetadata
				 yapClass, int classIndexID)
			{
				this._enclosing = _enclosing;
				this.yapClass = yapClass;
				this.classIndexID = classIndexID;
			}

			public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
			{
				yapClass.DefragClass(readers, classIndexID);
			}

			private readonly SecondPassCommand _enclosing;

			private readonly com.db4o.@internal.ClassMetadata yapClass;

			private readonly int classIndexID;
		}

		public void ProcessObjectSlot(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int id, bool registerAddresses)
		{
			com.db4o.@internal.ReaderPair.ProcessCopy(context, id, new _AnonymousInnerClass40
				(this, context), registerAddresses);
		}

		private sealed class _AnonymousInnerClass40 : com.db4o.@internal.SlotCopyHandler
		{
			public _AnonymousInnerClass40(SecondPassCommand _enclosing, com.db4o.defragment.DefragContextImpl
				 context)
			{
				this._enclosing = _enclosing;
				this.context = context;
			}

			public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
			{
				com.db4o.@internal.ClassMetadata.DefragObject(readers);
				if (this._enclosing._objectCommitFrequency > 0)
				{
					this._enclosing._objectCount++;
					if (this._enclosing._objectCount == this._enclosing._objectCommitFrequency)
					{
						context.TargetCommit();
						this._enclosing._objectCount = 0;
					}
				}
			}

			private readonly SecondPassCommand _enclosing;

			private readonly com.db4o.defragment.DefragContextImpl context;
		}

		public void ProcessClassCollection(com.db4o.defragment.DefragContextImpl context)
		{
			com.db4o.@internal.ReaderPair.ProcessCopy(context, context.SourceClassCollectionID
				(), new _AnonymousInnerClass55(this));
		}

		private sealed class _AnonymousInnerClass55 : com.db4o.@internal.SlotCopyHandler
		{
			public _AnonymousInnerClass55(SecondPassCommand _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
			{
				com.db4o.@internal.ClassMetadataRepository.Defrag(readers);
			}

			private readonly SecondPassCommand _enclosing;
		}

		public void ProcessBTree(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.btree.BTree
			 btree)
		{
			btree.DefragBTree(context);
		}

		public void Flush(com.db4o.defragment.DefragContextImpl context)
		{
		}
	}
}
