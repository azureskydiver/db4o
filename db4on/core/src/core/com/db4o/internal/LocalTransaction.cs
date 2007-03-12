namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class LocalTransaction : com.db4o.@internal.Transaction
	{
		private readonly byte[] _pointerBuffer = new byte[com.db4o.@internal.Const4.POINTER_LENGTH
			];

		private int i_address;

		private com.db4o.foundation.Tree _slotChanges;

		private com.db4o.foundation.Tree _writtenUpdateDeletedMembers;

		public LocalTransaction(com.db4o.@internal.ObjectContainerBase a_stream, com.db4o.@internal.Transaction
			 a_parent) : base(a_stream, a_parent)
		{
		}

		public override void Commit()
		{
			lock (Stream().i_lock)
			{
				if (!IsSystemTransaction())
				{
					TriggerCommitOnStarted();
				}
				i_file.FreeSpaceBeginCommit();
				CommitExceptForFreespace();
				i_file.FreeSpaceEndCommit();
			}
		}

		private void CommitExceptForFreespace()
		{
			Commit2Listeners();
			Commit3Stream();
			Commit4FieldIndexes();
			CommitParticipants();
			Stream().WriteDirty();
			Commit6WriteChanges();
			FreeOnCommit();
			Commit7ClearAll();
		}

		private void Commit2Listeners()
		{
			CommitParentListeners();
			CommitTransactionListeners();
		}

		private void CommitParentListeners()
		{
			if (i_parentTransaction != null)
			{
				ParentLocalTransaction().Commit2Listeners();
			}
		}

		private void Commit3Stream()
		{
			Stream().CheckNeededUpdates();
			Stream().WriteDirty();
			Stream().ClassCollection().Write(Stream().GetSystemTransaction());
		}

		private com.db4o.@internal.LocalTransaction ParentLocalTransaction()
		{
			return (com.db4o.@internal.LocalTransaction)i_parentTransaction;
		}

		private void Commit7ClearAll()
		{
			Commit7ParentClearAll();
			ClearAll();
		}

		private void Commit7ParentClearAll()
		{
			if (i_parentTransaction != null)
			{
				ParentLocalTransaction().Commit7ClearAll();
			}
		}

		protected override void ClearAll()
		{
			_slotChanges = null;
			base.ClearAll();
		}

		protected override void RollbackSlotChanges()
		{
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass104(this));
			}
		}

		private sealed class _AnonymousInnerClass104 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass104(LocalTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.@internal.slots.SlotChange)a_object).Rollback(this._enclosing.i_file);
			}

			private readonly LocalTransaction _enclosing;
		}

		public override bool IsDeleted(int id)
		{
			return SlotChangeIsFlaggedDeleted(id);
		}

		protected virtual void Commit6WriteChanges()
		{
			CheckSynchronization();
			int slotSetPointerCount = CountSlotChanges();
			if (slotSetPointerCount > 0)
			{
				int length = (((slotSetPointerCount * 3) + 2) * com.db4o.@internal.Const4.INT_LENGTH
					);
				int address = i_file.GetSlot(length);
				com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(this
					, address, length);
				bytes.WriteInt(length);
				bytes.WriteInt(slotSetPointerCount);
				AppendSlotChanges(bytes);
				bytes.Write();
				FlushFile();
				Stream().WriteTransactionPointer(address);
				FlushFile();
				if (WriteSlots())
				{
					FlushFile();
				}
				Stream().WriteTransactionPointer(0);
				FlushFile();
				i_file.Free(address, length);
			}
		}

		private bool WriteSlots()
		{
			CheckSynchronization();
			bool ret = false;
			if (i_parentTransaction != null)
			{
				if (ParentLocalTransaction().WriteSlots())
				{
					ret = true;
				}
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass160(this));
				ret = true;
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass160 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass160(LocalTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.@internal.slots.SlotChange)a_object).WritePointer(this._enclosing);
			}

			private readonly LocalTransaction _enclosing;
		}

		protected virtual void FlushFile()
		{
			if (i_file.ConfigImpl().FlushFileBuffers())
			{
				i_file.SyncFiles();
			}
		}

		private com.db4o.@internal.slots.SlotChange ProduceSlotChange(int id)
		{
			com.db4o.@internal.slots.SlotChange slot = new com.db4o.@internal.slots.SlotChange
				(id);
			_slotChanges = com.db4o.foundation.Tree.Add(_slotChanges, slot);
			return (com.db4o.@internal.slots.SlotChange)slot.AddedOrExisting();
		}

		private com.db4o.@internal.slots.SlotChange FindSlotChange(int a_id)
		{
			CheckSynchronization();
			return (com.db4o.@internal.slots.SlotChange)com.db4o.@internal.TreeInt.Find(_slotChanges
				, a_id);
		}

		public virtual com.db4o.@internal.slots.Slot GetCurrentSlotOfID(int id)
		{
			CheckSynchronization();
			if (id == 0)
			{
				return null;
			}
			com.db4o.@internal.slots.SlotChange change = FindSlotChange(id);
			if (change != null)
			{
				if (change.IsSetPointer())
				{
					return change.NewSlot();
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.@internal.slots.Slot parentSlot = ParentLocalTransaction().GetCurrentSlotOfID
					(id);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			return ReadCommittedSlotOfID(id);
		}

		public virtual com.db4o.@internal.slots.Slot GetCommittedSlotOfID(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.@internal.slots.SlotChange change = FindSlotChange(id);
			if (change != null)
			{
				com.db4o.@internal.slots.Slot slot = change.OldSlot();
				if (slot != null)
				{
					return slot;
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.@internal.slots.Slot parentSlot = ParentLocalTransaction().GetCommittedSlotOfID
					(id);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			return ReadCommittedSlotOfID(id);
		}

		private com.db4o.@internal.slots.Slot ReadCommittedSlotOfID(int id)
		{
			try
			{
				i_file.ReadBytes(_pointerBuffer, id, com.db4o.@internal.Const4.POINTER_LENGTH);
			}
			catch
			{
				return null;
			}
			int address = (_pointerBuffer[3] & 255) | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer
				[1] & 255) << 16 | _pointerBuffer[0] << 24;
			int length = (_pointerBuffer[7] & 255) | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer
				[5] & 255) << 16 | _pointerBuffer[4] << 24;
			return new com.db4o.@internal.slots.Slot(address, length);
		}

		public override void SetPointer(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			ProduceSlotChange(a_id).SetPointer(a_address, a_length);
		}

		private bool SlotChangeIsFlaggedDeleted(int id)
		{
			com.db4o.@internal.slots.SlotChange slot = FindSlotChange(id);
			if (slot != null)
			{
				return slot.IsDeleted();
			}
			if (i_parentTransaction != null)
			{
				return ParentLocalTransaction().SlotChangeIsFlaggedDeleted(id);
			}
			return false;
		}

		private int CountSlotChanges()
		{
			int count = 0;
			if (i_parentTransaction != null)
			{
				count += ParentLocalTransaction().CountSlotChanges();
			}
			int[] slotSetPointerCount = new int[] { count };
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass297(this, slotSetPointerCount));
			}
			return slotSetPointerCount[0];
		}

		private sealed class _AnonymousInnerClass297 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass297(LocalTransaction _enclosing, int[] slotSetPointerCount
				)
			{
				this._enclosing = _enclosing;
				this.slotSetPointerCount = slotSetPointerCount;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.slots.SlotChange slot = (com.db4o.@internal.slots.SlotChange)obj;
				if (slot.IsSetPointer())
				{
					slotSetPointerCount[0]++;
				}
			}

			private readonly LocalTransaction _enclosing;

			private readonly int[] slotSetPointerCount;
		}

		internal virtual void WriteOld()
		{
			lock (Stream().i_lock)
			{
				i_pointerIo.UseSlot(i_address);
				i_pointerIo.Read();
				int length = i_pointerIo.ReadInt();
				if (length > 0)
				{
					com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(this
						, i_address, length);
					bytes.Read();
					bytes.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
					_slotChanges = new com.db4o.@internal.TreeReader(bytes, new com.db4o.@internal.slots.SlotChange
						(0)).Read();
					if (WriteSlots())
					{
						FlushFile();
					}
					Stream().WriteTransactionPointer(0);
					FlushFile();
					FreeOnCommit();
				}
				else
				{
					Stream().WriteTransactionPointer(0);
					FlushFile();
				}
			}
		}

		protected sealed override void FreeOnCommit()
		{
			CheckSynchronization();
			if (i_parentTransaction != null)
			{
				ParentLocalTransaction().FreeOnCommit();
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass339(this));
			}
		}

		private sealed class _AnonymousInnerClass339 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass339(LocalTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				((com.db4o.@internal.slots.SlotChange)obj).FreeDuringCommit(this._enclosing.i_file
					);
			}

			private readonly LocalTransaction _enclosing;
		}

		private void AppendSlotChanges(com.db4o.@internal.Buffer writer)
		{
			if (i_parentTransaction != null)
			{
				ParentLocalTransaction().AppendSlotChanges(writer);
			}
			com.db4o.foundation.Tree.Traverse(_slotChanges, new _AnonymousInnerClass353(this, 
				writer));
		}

		private sealed class _AnonymousInnerClass353 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass353(LocalTransaction _enclosing, com.db4o.@internal.Buffer
				 writer)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
			}

			public void Visit(object obj)
			{
				((com.db4o.@internal.TreeInt)obj).Write(writer);
			}

			private readonly LocalTransaction _enclosing;

			private readonly com.db4o.@internal.Buffer writer;
		}

		public override void SlotDelete(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			if (a_id == 0)
			{
				return;
			}
			com.db4o.@internal.slots.SlotChange slot = ProduceSlotChange(a_id);
			slot.FreeOnCommit(i_file, new com.db4o.@internal.slots.Slot(a_address, a_length));
			slot.SetPointer(0, 0);
		}

		private void SlotFreeOnCommit(int id, com.db4o.@internal.slots.Slot slot)
		{
			if (slot == null)
			{
				return;
			}
			SlotFreeOnCommit(id, slot.GetAddress(), slot.GetLength());
		}

		public override void SlotFreeOnCommit(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			if (a_id == 0)
			{
				return;
			}
			ProduceSlotChange(a_id).FreeOnCommit(i_file, new com.db4o.@internal.slots.Slot(a_address
				, a_length));
		}

		public override void SlotFreeOnRollback(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			ProduceSlotChange(a_id).FreeOnRollback(a_address, a_length);
		}

		internal override void SlotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			com.db4o.@internal.slots.Slot slot = GetCurrentSlotOfID(a_id);
			if (slot == null)
			{
				return;
			}
			CheckSynchronization();
			com.db4o.@internal.slots.SlotChange change = ProduceSlotChange(a_id);
			change.FreeOnRollbackSetPointer(newAddress, newLength);
			change.FreeOnCommit(i_file, slot);
		}

		internal override void ProduceUpdateSlotChange(int a_id, int a_address, int a_length
			)
		{
			CheckSynchronization();
			com.db4o.@internal.slots.SlotChange slotChange = ProduceSlotChange(a_id);
			slotChange.FreeOnRollbackSetPointer(a_address, a_length);
		}

		public override void SlotFreePointerOnCommit(int a_id)
		{
			CheckSynchronization();
			com.db4o.@internal.slots.Slot slot = GetCurrentSlotOfID(a_id);
			if (slot == null)
			{
				return;
			}
			SlotFreeOnCommit(a_id, slot._address, slot._length);
		}

		internal override void SlotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
			CheckSynchronization();
			SlotFreeOnCommit(a_address, a_address, a_length);
			SlotFreeOnCommit(a_id, a_id, com.db4o.@internal.Const4.POINTER_LENGTH);
		}

		public override void SlotFreePointerOnRollback(int id)
		{
			ProduceSlotChange(id).FreePointerOnRollback();
		}

		public override void ProcessDeletes()
		{
			if (i_delete == null)
			{
				_writtenUpdateDeletedMembers = null;
				return;
			}
			while (i_delete != null)
			{
				com.db4o.foundation.Tree delete = i_delete;
				i_delete = null;
				delete.Traverse(new _AnonymousInnerClass477(this));
			}
			_writtenUpdateDeletedMembers = null;
		}

		private sealed class _AnonymousInnerClass477 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass477(LocalTransaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.@internal.DeleteInfo info = (com.db4o.@internal.DeleteInfo)a_object;
				if (this._enclosing.IsDeleted(info._key))
				{
					return;
				}
				object obj = null;
				if (info._reference != null)
				{
					obj = info._reference.GetObject();
				}
				if (obj == null)
				{
					com.db4o.@internal.HardObjectReference hardRef = this._enclosing.Stream().GetHardObjectReferenceById
						(this._enclosing, info._key);
					info._reference = hardRef._reference;
					info._reference.FlagForDelete(this._enclosing.Stream().TopLevelCallId());
				}
				this._enclosing.Stream().Delete3(this._enclosing, info._reference, info._cascade, 
					false);
			}

			private readonly LocalTransaction _enclosing;
		}

		public override void WriteUpdateDeleteMembers(int id, com.db4o.@internal.ClassMetadata
			 clazz, int typeInfo, int cascade)
		{
			CheckSynchronization();
			com.db4o.@internal.TreeInt newNode = new com.db4o.@internal.TreeInt(id);
			_writtenUpdateDeletedMembers = com.db4o.foundation.Tree.Add(_writtenUpdateDeletedMembers
				, newNode);
			if (!newNode.WasAddedToTree())
			{
				return;
			}
			if (clazz.CanUpdateFast())
			{
				SlotFreeOnCommit(id, GetCurrentSlotOfID(id));
				return;
			}
			com.db4o.@internal.StatefulBuffer objectBytes = Stream().ReadWriterByID(this, id);
			if (objectBytes == null)
			{
				if (clazz.HasIndex())
				{
					DontRemoveFromClassIndex(clazz.GetID(), id);
				}
				return;
			}
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(Stream(), clazz, objectBytes);
			com.db4o.@internal.DeleteInfo info = (com.db4o.@internal.DeleteInfo)com.db4o.@internal.TreeInt
				.Find(i_delete, id);
			if (info != null)
			{
				if (info._cascade > cascade)
				{
					cascade = info._cascade;
				}
			}
			objectBytes.SetCascadeDeletes(cascade);
			clazz.DeleteMembers(oh._marshallerFamily, oh._headerAttributes, objectBytes, typeInfo
				, true);
			SlotFreeOnCommit(id, new com.db4o.@internal.slots.Slot(objectBytes.GetAddress(), 
				objectBytes.GetLength()));
		}

		private void TriggerCommitOnStarted()
		{
			com.db4o.@internal.callbacks.Callbacks callbacks = Stream().Callbacks();
			if (!callbacks.CaresAboutCommit())
			{
				return;
			}
			com.db4o.ext.ObjectInfoCollection[] collections = PartitionSlotChangesInAddedDeletedUpdated
				();
			callbacks.CommitOnStarted(collections[0], collections[1], collections[2]);
		}

		private sealed class ObjectInfoCollectionImpl : com.db4o.ext.ObjectInfoCollection
		{
			public static readonly com.db4o.ext.ObjectInfoCollection EMPTY = new com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl
				(com.db4o.foundation.Iterators.EMPTY_ITERABLE);

			private readonly System.Collections.IEnumerable _collection;

			public ObjectInfoCollectionImpl(System.Collections.IEnumerable collection)
			{
				_collection = collection;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return _collection.GetEnumerator();
			}
		}

		private com.db4o.ext.ObjectInfoCollection[] PartitionSlotChangesInAddedDeletedUpdated
			()
		{
			if (null == _slotChanges)
			{
				return new com.db4o.ext.ObjectInfoCollection[] { com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl
					.EMPTY, com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl.EMPTY, com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl
					.EMPTY };
			}
			com.db4o.foundation.Collection4 added = new com.db4o.foundation.Collection4();
			com.db4o.foundation.Collection4 deleted = new com.db4o.foundation.Collection4();
			com.db4o.foundation.Collection4 updated = new com.db4o.foundation.Collection4();
			_slotChanges.Traverse(new _AnonymousInnerClass588(this, deleted, added, updated));
			return new com.db4o.ext.ObjectInfoCollection[] { new com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl
				(added), new com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl(deleted
				), new com.db4o.@internal.LocalTransaction.ObjectInfoCollectionImpl(updated) };
		}

		private sealed class _AnonymousInnerClass588 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass588(LocalTransaction _enclosing, com.db4o.foundation.Collection4
				 deleted, com.db4o.foundation.Collection4 added, com.db4o.foundation.Collection4
				 updated)
			{
				this._enclosing = _enclosing;
				this.deleted = deleted;
				this.added = added;
				this.updated = updated;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.slots.SlotChange slotChange = ((com.db4o.@internal.slots.SlotChange
					)obj);
				com.db4o.@internal.LazyObjectReference lazyRef = new com.db4o.@internal.LazyObjectReference
					(this._enclosing.Stream(), slotChange._key);
				if (slotChange.IsDeleted())
				{
					deleted.Add(lazyRef);
				}
				else
				{
					if (slotChange.IsNew())
					{
						added.Add(lazyRef);
					}
					else
					{
						updated.Add(lazyRef);
					}
				}
			}

			private readonly LocalTransaction _enclosing;

			private readonly com.db4o.foundation.Collection4 deleted;

			private readonly com.db4o.foundation.Collection4 added;

			private readonly com.db4o.foundation.Collection4 updated;
		}

		private void SetAddress(int a_address)
		{
			i_address = a_address;
		}

		public static com.db4o.@internal.Transaction ReadInterruptedTransaction(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader)
		{
			int transactionID1 = reader.ReadInt();
			int transactionID2 = reader.ReadInt();
			if ((transactionID1 > 0) && (transactionID1 == transactionID2))
			{
				com.db4o.@internal.LocalTransaction transaction = (com.db4o.@internal.LocalTransaction
					)file.NewTransaction(null);
				transaction.SetAddress(transactionID1);
				return transaction;
			}
			return null;
		}
	}
}
