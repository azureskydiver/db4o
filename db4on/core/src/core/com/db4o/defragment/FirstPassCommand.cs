namespace com.db4o.defragment
{
	/// <summary>
	/// First step in the defragmenting process: Allocates pointer slots in the target file for
	/// each ID (but doesn't fill them in, yet) and registers the mapping from source pointer address
	/// to target pointer address.
	/// </summary>
	/// <remarks>
	/// First step in the defragmenting process: Allocates pointer slots in the target file for
	/// each ID (but doesn't fill them in, yet) and registers the mapping from source pointer address
	/// to target pointer address.
	/// </remarks>
	/// <exclude></exclude>
	internal sealed class FirstPassCommand : com.db4o.defragment.PassCommand
	{
		private const int ID_BATCH_SIZE = 4096;

		private com.db4o.@internal.TreeInt _ids;

		internal void Process(com.db4o.defragment.DefragContextImpl context, int objectID
			, bool isClassID)
		{
			if (BatchFull())
			{
				Flush(context);
			}
			_ids = com.db4o.@internal.TreeInt.Add(_ids, (isClassID ? -objectID : objectID));
		}

		private bool BatchFull()
		{
			return _ids != null && _ids.Size() == ID_BATCH_SIZE;
		}

		public void ProcessClass(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int id, int classIndexID)
		{
			Process(context, id, true);
			for (int fieldIdx = 0; fieldIdx < yapClass.i_fields.Length; fieldIdx++)
			{
				com.db4o.@internal.FieldMetadata field = yapClass.i_fields[fieldIdx];
				if (!field.IsVirtual() && field.HasIndex())
				{
					ProcessBTree(context, field.GetIndex(context.SystemTrans()));
				}
			}
		}

		public void ProcessObjectSlot(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.ClassMetadata
			 yapClass, int sourceID, bool registerAddresses)
		{
			Process(context, sourceID, false);
		}

		public void ProcessClassCollection(com.db4o.defragment.DefragContextImpl context)
		{
			Process(context, context.SourceClassCollectionID(), false);
		}

		public void ProcessBTree(com.db4o.defragment.DefragContextImpl context, com.db4o.@internal.btree.BTree
			 btree)
		{
			Process(context, btree.GetID(), false);
			context.TraverseAllIndexSlots(btree, new _AnonymousInnerClass54(this, context));
		}

		private sealed class _AnonymousInnerClass54 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass54(FirstPassCommand _enclosing, com.db4o.defragment.DefragContextImpl
				 context)
			{
				this._enclosing = _enclosing;
				this.context = context;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				this._enclosing.Process(context, id, false);
			}

			private readonly FirstPassCommand _enclosing;

			private readonly com.db4o.defragment.DefragContextImpl context;
		}

		public void Flush(com.db4o.defragment.DefragContextImpl context)
		{
			if (_ids == null)
			{
				return;
			}
			int pointerAddress = context.AllocateTargetSlot(_ids.Size() * com.db4o.@internal.Const4
				.POINTER_LENGTH);
			System.Collections.IEnumerator idIter = new com.db4o.foundation.TreeKeyIterator(_ids
				);
			while (idIter.MoveNext())
			{
				int objectID = ((int)idIter.Current);
				bool isClassID = false;
				if (objectID < 0)
				{
					objectID = -objectID;
					isClassID = true;
				}
				context.MapIDs(objectID, pointerAddress, isClassID);
				pointerAddress += com.db4o.@internal.Const4.POINTER_LENGTH;
			}
			_ids = null;
		}
	}
}
