namespace com.db4o
{
	/// <exclude></exclude>
	public class Transaction
	{
		private com.db4o.foundation.Tree _slotChanges;

		private int i_address;

		private readonly byte[] _pointerBuffer = new byte[com.db4o.YapConst.POINTER_LENGTH
			];

		public com.db4o.foundation.Tree i_delete;

		private com.db4o.foundation.List4 i_dirtyFieldIndexes;

		public readonly com.db4o.YapFile i_file;

		internal readonly com.db4o.Transaction i_parentTransaction;

		private readonly com.db4o.YapWriter i_pointerIo;

		private readonly com.db4o.YapStream i_stream;

		private com.db4o.foundation.List4 i_transactionListeners;

		protected com.db4o.foundation.Tree i_writtenUpdateDeletedMembers;

		private readonly com.db4o.foundation.Collection4 _participants = new com.db4o.foundation.Collection4
			();

		internal Transaction(com.db4o.YapStream a_stream, com.db4o.Transaction a_parent)
		{
			i_stream = a_stream;
			i_file = (a_stream is com.db4o.YapFile) ? (com.db4o.YapFile)a_stream : null;
			i_parentTransaction = a_parent;
			i_pointerIo = new com.db4o.YapWriter(this, com.db4o.YapConst.POINTER_LENGTH);
		}

		public virtual void AddDirtyFieldIndex(com.db4o.inside.ix.IndexTransaction a_xft)
		{
			i_dirtyFieldIndexes = new com.db4o.foundation.List4(i_dirtyFieldIndexes, a_xft);
		}

		public void CheckSynchronization()
		{
		}

		public virtual void AddTransactionListener(com.db4o.TransactionListener a_listener
			)
		{
			i_transactionListeners = new com.db4o.foundation.List4(i_transactionListeners, a_listener
				);
		}

		private void AppendSlotChanges(com.db4o.YapWriter writer)
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.AppendSlotChanges(writer);
			}
			com.db4o.foundation.Tree.Traverse(_slotChanges, new _AnonymousInnerClass72(this, 
				writer));
		}

		private sealed class _AnonymousInnerClass72 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass72(Transaction _enclosing, com.db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
			}

			public void Visit(object obj)
			{
				((com.db4o.TreeInt)obj).Write(writer);
			}

			private readonly Transaction _enclosing;

			private readonly com.db4o.YapWriter writer;
		}

		internal virtual void BeginEndSet()
		{
			CheckSynchronization();
			if (i_delete != null)
			{
				bool[] foundOne = { false };
				com.db4o.Transaction finalThis = this;
				do
				{
					foundOne[0] = false;
					com.db4o.foundation.Tree delete = i_delete;
					i_delete = null;
					delete.Traverse(new _AnonymousInnerClass91(this));
					delete.Traverse(new _AnonymousInnerClass101(this, foundOne, finalThis));
				}
				while (foundOne[0]);
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
		}

		private sealed class _AnonymousInnerClass91 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass91(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (!info._delete)
				{
					this._enclosing.i_delete = com.db4o.foundation.Tree.Add(this._enclosing.i_delete, 
						new com.db4o.DeleteInfo(info._key, null, false, info._cascade));
				}
			}

			private readonly Transaction _enclosing;
		}

		private sealed class _AnonymousInnerClass101 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass101(Transaction _enclosing, bool[] foundOne, com.db4o.Transaction
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.foundOne = foundOne;
				this.finalThis = finalThis;
			}

			public void Visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (info._delete)
				{
					foundOne[0] = true;
					object obj = null;
					if (info._reference != null)
					{
						obj = info._reference.GetObject();
					}
					if (obj == null)
					{
						object[] arr = finalThis.Stream().GetObjectAndYapObjectByID(finalThis, info._key);
						obj = arr[0];
						info._reference = (com.db4o.YapObject)arr[1];
					}
					this._enclosing.Stream().Delete4(finalThis, info._reference, info._cascade, false
						);
				}
				this._enclosing.i_delete = com.db4o.foundation.Tree.Add(this._enclosing.i_delete, 
					new com.db4o.DeleteInfo(info._key, null, false, info._cascade));
			}

			private readonly Transaction _enclosing;

			private readonly bool[] foundOne;

			private readonly com.db4o.Transaction finalThis;
		}

		private void ClearAll()
		{
			_slotChanges = null;
			i_dirtyFieldIndexes = null;
			i_transactionListeners = null;
			DisposeParticipants();
			_participants.Clear();
		}

		private void DisposeParticipants()
		{
			com.db4o.foundation.Iterator4 iterator = _participants.Iterator();
			while (iterator.MoveNext())
			{
				((com.db4o.TransactionParticipant)iterator.Current()).Dispose(this);
			}
		}

		internal virtual void Close(bool a_rollbackOnClose)
		{
			try
			{
				if (Stream() != null)
				{
					CheckSynchronization();
					Stream().ReleaseSemaphores(this);
				}
			}
			catch (System.Exception e)
			{
			}
			if (a_rollbackOnClose)
			{
				try
				{
					Rollback();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		public virtual void Commit()
		{
			lock (Stream().i_lock)
			{
				i_file.FreeSpaceBeginCommit();
				CommitExceptForFreespace();
				i_file.FreeSpaceEndCommit();
			}
		}

		private void CommitExceptForFreespace()
		{
			Commit1BeginEndSet();
			Commit2Listeners();
			Commit3Stream();
			Commit4FieldIndexes();
			Commit5Participants();
			Stream().WriteDirty();
			Commit6WriteChanges();
			FreeOnCommit();
			Commit7ClearAll();
		}

		private void Commit1BeginEndSet()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit1BeginEndSet();
			}
			BeginEndSet();
		}

		private void Commit2Listeners()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit2Listeners();
			}
			CommitTransactionListeners();
		}

		private void Commit3Stream()
		{
			Stream().CheckNeededUpdates();
			Stream().WriteDirty();
			Stream().ClassCollection().Write(Stream().GetSystemTransaction());
		}

		private void Commit4FieldIndexes()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit4FieldIndexes();
			}
			if (i_dirtyFieldIndexes != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.MoveNext())
				{
					((com.db4o.inside.ix.IndexTransaction)i.Current()).Commit();
				}
			}
		}

		private void Commit5Participants()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit5Participants();
			}
			com.db4o.foundation.Iterator4 iterator = _participants.Iterator();
			while (iterator.MoveNext())
			{
				((com.db4o.TransactionParticipant)iterator.Current()).Commit(this);
			}
		}

		private void Commit6WriteChanges()
		{
			CheckSynchronization();
			int slotSetPointerCount = CountSlotChanges();
			if (slotSetPointerCount > 0)
			{
				int length = (((slotSetPointerCount * 3) + 2) * com.db4o.YapConst.INT_LENGTH);
				int address = i_file.GetSlot(length);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, address, length);
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

		private void Commit7ClearAll()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit7ClearAll();
			}
			ClearAll();
		}

		internal virtual void CommitTransactionListeners()
		{
			CheckSynchronization();
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.MoveNext())
				{
					((com.db4o.TransactionListener)i.Current()).PreCommit();
				}
				i_transactionListeners = null;
			}
		}

		private int CountSlotChanges()
		{
			int count = 0;
			if (i_parentTransaction != null)
			{
				count += i_parentTransaction.CountSlotChanges();
			}
			int[] slotSetPointerCount = { count };
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass310(this, slotSetPointerCount));
			}
			return slotSetPointerCount[0];
		}

		private sealed class _AnonymousInnerClass310 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass310(Transaction _enclosing, int[] slotSetPointerCount)
			{
				this._enclosing = _enclosing;
				this.slotSetPointerCount = slotSetPointerCount;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.slots.SlotChange slot = (com.db4o.inside.slots.SlotChange)obj;
				if (slot.IsSetPointer())
				{
					slotSetPointerCount[0]++;
				}
			}

			private readonly Transaction _enclosing;

			private readonly int[] slotSetPointerCount;
		}

		internal virtual void Delete(com.db4o.YapObject a_yo, int a_cascade)
		{
			CheckSynchronization();
			int id = a_yo.GetID();
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, id
				);
			if (info == null)
			{
				info = new com.db4o.DeleteInfo(id, a_yo, true, a_cascade);
				i_delete = com.db4o.foundation.Tree.Add(i_delete, info);
				return;
			}
			info._reference = a_yo;
			if (a_cascade > info._cascade)
			{
				info._cascade = a_cascade;
			}
		}

		internal virtual void DontDelete(int classID, int a_id)
		{
			CheckSynchronization();
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, a_id
				);
			if (info == null)
			{
				i_delete = com.db4o.foundation.Tree.Add(i_delete, new com.db4o.DeleteInfo(a_id, null
					, false, 0));
			}
			else
			{
				info._delete = false;
			}
			com.db4o.YapClass yc = Stream().GetYapClass(classID);
			DontDeleteAllAncestors(yc, a_id);
		}

		internal virtual void DontDeleteAllAncestors(com.db4o.YapClass yapClass, int objectID
			)
		{
			if (yapClass == null)
			{
				return;
			}
			yapClass.Index().DontDelete(this, objectID);
			DontDeleteAllAncestors(yapClass.i_ancestor, objectID);
		}

		internal virtual void DontRemoveFromClassIndex(int a_yapClassID, int a_id)
		{
			CheckSynchronization();
			com.db4o.YapClass yapClass = Stream().GetYapClass(a_yapClassID);
			yapClass.Index().Add(this, a_id);
		}

		private com.db4o.inside.slots.SlotChange FindSlotChange(int a_id)
		{
			CheckSynchronization();
			return (com.db4o.inside.slots.SlotChange)com.db4o.TreeInt.Find(_slotChanges, a_id
				);
		}

		private void FlushFile()
		{
			if (i_file.ConfigImpl().FlushFileBuffers())
			{
				i_file.SyncFiles();
			}
		}

		private void FreeOnCommit()
		{
			CheckSynchronization();
			if (i_parentTransaction != null)
			{
				i_parentTransaction.FreeOnCommit();
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass393(this));
			}
		}

		private sealed class _AnonymousInnerClass393 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass393(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				((com.db4o.inside.slots.SlotChange)obj).FreeDuringCommit(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		public virtual com.db4o.inside.slots.Slot GetCurrentSlotOfID(int id)
		{
			CheckSynchronization();
			if (id == 0)
			{
				return null;
			}
			com.db4o.inside.slots.SlotChange change = FindSlotChange(id);
			if (change != null)
			{
				if (change.IsSetPointer())
				{
					return change.NewSlot();
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.inside.slots.Slot parentSlot = i_parentTransaction.GetCurrentSlotOfID(id
					);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			return ReadCommittedSlotOfID(id);
		}

		public virtual com.db4o.inside.slots.Slot GetCommittedSlotOfID(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.inside.slots.SlotChange change = FindSlotChange(id);
			if (change != null)
			{
				com.db4o.inside.slots.Slot slot = change.OldSlot();
				if (slot != null)
				{
					return slot;
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.inside.slots.Slot parentSlot = i_parentTransaction.GetCommittedSlotOfID(
					id);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			return ReadCommittedSlotOfID(id);
		}

		private com.db4o.inside.slots.Slot ReadCommittedSlotOfID(int id)
		{
			i_file.ReadBytes(_pointerBuffer, id, com.db4o.YapConst.POINTER_LENGTH);
			int address = (_pointerBuffer[3] & 255) | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer
				[1] & 255) << 16 | _pointerBuffer[0] << 24;
			int length = (_pointerBuffer[7] & 255) | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer
				[5] & 255) << 16 | _pointerBuffer[4] << 24;
			return new com.db4o.inside.slots.Slot(address, length);
		}

		internal virtual bool IsDeleted(int a_id)
		{
			CheckSynchronization();
			com.db4o.inside.slots.SlotChange slot = FindSlotChange(a_id);
			if (slot != null)
			{
				return slot.IsDeleted();
			}
			if (i_parentTransaction != null)
			{
				return i_parentTransaction.IsDeleted(a_id);
			}
			return false;
		}

		internal virtual object[] ObjectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			CheckSynchronization();
			return Stream().GetFieldUUID().ObjectAndYapObjectBySignature(this, a_uuid, a_signature
				);
		}

		private com.db4o.inside.slots.SlotChange ProduceSlotChange(int id)
		{
			com.db4o.inside.slots.SlotChange slot = new com.db4o.inside.slots.SlotChange(id);
			_slotChanges = com.db4o.foundation.Tree.Add(_slotChanges, slot);
			return (com.db4o.inside.slots.SlotChange)slot.DuplicateOrThis();
		}

		internal virtual com.db4o.reflect.Reflector Reflector()
		{
			return Stream().Reflector();
		}

		public virtual void Rollback()
		{
			lock (Stream().i_lock)
			{
				BeginEndSet();
				RollbackParticipants();
				RollbackFieldIndexes();
				RollbackSlotChanges();
				RollBackTransactionListeners();
				ClearAll();
			}
		}

		private void RollbackSlotChanges()
		{
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass509(this));
			}
		}

		private sealed class _AnonymousInnerClass509 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass509(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).Rollback(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		private void RollbackFieldIndexes()
		{
			if (i_dirtyFieldIndexes != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.MoveNext())
				{
					((com.db4o.inside.ix.IndexTransaction)i.Current()).Rollback();
				}
			}
		}

		private void RollbackParticipants()
		{
			com.db4o.foundation.Iterator4 iterator = _participants.Iterator();
			while (iterator.MoveNext())
			{
				((com.db4o.TransactionParticipant)iterator.Current()).Rollback(this);
			}
		}

		internal virtual void RollBackTransactionListeners()
		{
			CheckSynchronization();
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.MoveNext())
				{
					((com.db4o.TransactionListener)i.Current()).PostRollback();
				}
				i_transactionListeners = null;
			}
		}

		internal virtual void SetAddress(int a_address)
		{
			i_address = a_address;
		}

		public virtual void SetPointer(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			ProduceSlotChange(a_id).SetPointer(a_address, a_length);
		}

		internal virtual void SlotDelete(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			if (a_id == 0)
			{
				return;
			}
			com.db4o.inside.slots.SlotChange slot = ProduceSlotChange(a_id);
			slot.FreeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address, a_length));
			slot.SetPointer(0, 0);
		}

		public virtual void SlotFreeOnCommit(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			if (a_id == 0)
			{
				return;
			}
			ProduceSlotChange(a_id).FreeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address
				, a_length));
		}

		internal virtual void SlotFreeOnRollback(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			ProduceSlotChange(a_id).FreeOnRollback(a_address, a_length);
		}

		internal virtual void SlotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			com.db4o.inside.slots.Slot slot = GetCurrentSlotOfID(a_id);
			CheckSynchronization();
			com.db4o.inside.slots.SlotChange change = ProduceSlotChange(a_id);
			change.FreeOnRollbackSetPointer(newAddress, newLength);
			change.FreeOnCommit(i_file, slot);
		}

		internal virtual void SlotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length
			)
		{
			CheckSynchronization();
			ProduceSlotChange(a_id).FreeOnRollbackSetPointer(a_address, a_length);
		}

		public virtual void SlotFreePointerOnCommit(int a_id)
		{
			CheckSynchronization();
			com.db4o.inside.slots.Slot slot = GetCurrentSlotOfID(a_id);
			if (slot == null)
			{
				return;
			}
			SlotFreeOnCommit(a_id, slot._address, slot._length);
		}

		internal virtual void SlotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
			CheckSynchronization();
			SlotFreeOnCommit(a_address, a_address, a_length);
			SlotFreeOnCommit(a_id, a_id, com.db4o.YapConst.POINTER_LENGTH);
		}

		internal virtual bool SupportsVirtualFields()
		{
			return true;
		}

		public virtual com.db4o.Transaction SystemTransaction()
		{
			if (i_parentTransaction != null)
			{
				return i_parentTransaction;
			}
			return this;
		}

		public override string ToString()
		{
			return Stream().ToString();
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
					com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, i_address, length);
					bytes.Read();
					bytes.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
					_slotChanges = new com.db4o.TreeReader(bytes, new com.db4o.inside.slots.SlotChange
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

		public virtual void WritePointer(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			i_pointerIo.UseSlot(a_id);
			i_pointerIo.WriteInt(a_address);
			i_pointerIo.WriteInt(a_length);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				i_pointerIo.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			i_pointerIo.Write();
		}

		private bool WriteSlots()
		{
			CheckSynchronization();
			bool ret = false;
			if (i_parentTransaction != null)
			{
				if (i_parentTransaction.WriteSlots())
				{
					ret = true;
				}
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass707(this));
				ret = true;
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass707 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass707(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).WritePointer(this._enclosing);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void WriteUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			CheckSynchronization();
			if (com.db4o.foundation.Tree.Find(i_writtenUpdateDeletedMembers, new com.db4o.TreeInt
				(a_id)) != null)
			{
				return;
			}
			i_writtenUpdateDeletedMembers = com.db4o.foundation.Tree.Add(i_writtenUpdateDeletedMembers
				, new com.db4o.TreeInt(a_id));
			com.db4o.YapWriter objectBytes = Stream().ReadWriterByID(this, a_id);
			if (objectBytes == null)
			{
				if (a_yc.HasIndex())
				{
					DontRemoveFromClassIndex(a_yc.GetID(), a_id);
				}
				return;
			}
			com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
				(Stream(), a_yc, objectBytes);
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, a_id
				);
			if (info != null)
			{
				if (info._cascade > a_cascade)
				{
					a_cascade = info._cascade;
				}
			}
			objectBytes.SetCascadeDeletes(a_cascade);
			a_yc.DeleteMembers(oh._marshallerFamily, oh._headerAttributes, objectBytes, a_type
				, true);
			SlotFreeOnCommit(a_id, objectBytes.GetAddress(), objectBytes.GetLength());
		}

		public virtual com.db4o.YapStream Stream()
		{
			return i_stream;
		}

		public virtual void Enlist(com.db4o.TransactionParticipant participant)
		{
			if (null == participant)
			{
				throw new System.ArgumentNullException("participant");
			}
			CheckSynchronization();
			if (!_participants.ContainsByIdentity(participant))
			{
				_participants.Add(participant);
			}
		}
	}
}
