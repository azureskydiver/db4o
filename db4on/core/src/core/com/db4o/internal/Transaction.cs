namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public abstract class Transaction
	{
		protected int i_address;

		protected readonly byte[] _pointerBuffer = new byte[com.db4o.@internal.Const4.POINTER_LENGTH
			];

		public com.db4o.foundation.Tree i_delete;

		private com.db4o.foundation.List4 i_dirtyFieldIndexes;

		public readonly com.db4o.@internal.LocalObjectContainer i_file;

		internal readonly com.db4o.@internal.Transaction i_parentTransaction;

		protected readonly com.db4o.@internal.StatefulBuffer i_pointerIo;

		private readonly com.db4o.@internal.ObjectContainerBase i_stream;

		private com.db4o.foundation.List4 i_transactionListeners;

		protected com.db4o.foundation.Tree i_writtenUpdateDeletedMembers;

		private readonly com.db4o.foundation.Collection4 _participants = new com.db4o.foundation.Collection4
			();

		public Transaction(com.db4o.@internal.ObjectContainerBase a_stream, com.db4o.@internal.Transaction
			 a_parent)
		{
			i_stream = a_stream;
			i_file = (a_stream is com.db4o.@internal.LocalObjectContainer) ? (com.db4o.@internal.LocalObjectContainer
				)a_stream : null;
			i_parentTransaction = a_parent;
			i_pointerIo = new com.db4o.@internal.StatefulBuffer(this, com.db4o.@internal.Const4
				.POINTER_LENGTH);
		}

		public virtual void AddDirtyFieldIndex(com.db4o.@internal.ix.IndexTransaction a_xft
			)
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

		protected virtual void ClearAll()
		{
			i_dirtyFieldIndexes = null;
			i_transactionListeners = null;
			DisposeParticipants();
			_participants.Clear();
		}

		private void DisposeParticipants()
		{
			System.Collections.IEnumerator iterator = _participants.GetEnumerator();
			while (iterator.MoveNext())
			{
				((com.db4o.@internal.TransactionParticipant)iterator.Current).Dispose(this);
			}
		}

		public virtual void Close(bool a_rollbackOnClose)
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
			Commit2Listeners();
			Commit3Stream();
			Commit4FieldIndexes();
			Commit5Participants();
			Stream().WriteDirty();
			Commit6WriteChanges();
			FreeOnCommit();
			Commit7ClearAll();
		}

		protected virtual void FreeOnCommit()
		{
		}

		protected virtual void Commit6WriteChanges()
		{
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
				i_parentTransaction.Commit7ClearAll();
			}
		}

		private void Commit2Listeners()
		{
			Commit2ParentListeners();
			CommitTransactionListeners();
		}

		private void Commit2ParentListeners()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit2Listeners();
			}
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
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.MoveNext())
				{
					((com.db4o.@internal.ix.IndexTransaction)i.Current).Commit();
				}
			}
		}

		private void Commit5Participants()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit5Participants();
			}
			System.Collections.IEnumerator iterator = _participants.GetEnumerator();
			while (iterator.MoveNext())
			{
				((com.db4o.@internal.TransactionParticipant)iterator.Current).Commit(this);
			}
		}

		protected virtual void CommitTransactionListeners()
		{
			CheckSynchronization();
			if (i_transactionListeners != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.MoveNext())
				{
					((com.db4o.TransactionListener)i.Current).PreCommit();
				}
				i_transactionListeners = null;
			}
		}

		public abstract bool IsDeleted(int id);

		public virtual bool Delete(com.db4o.@internal.ObjectReference @ref, int id, int cascade
			)
		{
			CheckSynchronization();
			if (@ref != null)
			{
				if (!i_stream.FlagForDelete(@ref))
				{
					return false;
				}
			}
			com.db4o.@internal.DeleteInfo info = (com.db4o.@internal.DeleteInfo)com.db4o.@internal.TreeInt
				.Find(i_delete, id);
			if (info == null)
			{
				info = new com.db4o.@internal.DeleteInfo(id, @ref, cascade);
				i_delete = com.db4o.foundation.Tree.Add(i_delete, info);
				return true;
			}
			info._reference = @ref;
			if (cascade > info._cascade)
			{
				info._cascade = cascade;
			}
			return true;
		}

		public virtual void DontDelete(int a_id)
		{
			if (i_delete == null)
			{
				return;
			}
			i_delete = com.db4o.@internal.TreeInt.RemoveLike((com.db4o.@internal.TreeInt)i_delete
				, a_id);
		}

		internal virtual void DontRemoveFromClassIndex(int a_yapClassID, int a_id)
		{
			CheckSynchronization();
			com.db4o.@internal.ClassMetadata yapClass = Stream().GetYapClass(a_yapClassID);
			yapClass.Index().Add(this, a_id);
		}

		public virtual object[] ObjectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			CheckSynchronization();
			return Stream().GetUUIDIndex().ObjectAndYapObjectBySignature(this, a_uuid, a_signature
				);
		}

		public abstract void ProcessDeletes();

		public virtual com.db4o.reflect.Reflector Reflector()
		{
			return Stream().Reflector();
		}

		public virtual void Rollback()
		{
			lock (Stream().i_lock)
			{
				RollbackParticipants();
				RollbackFieldIndexes();
				RollbackSlotChanges();
				RollBackTransactionListeners();
				ClearAll();
			}
		}

		protected virtual void RollbackSlotChanges()
		{
		}

		private void RollbackFieldIndexes()
		{
			if (i_dirtyFieldIndexes != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.MoveNext())
				{
					((com.db4o.@internal.ix.IndexTransaction)i.Current).Rollback();
				}
			}
		}

		private void RollbackParticipants()
		{
			System.Collections.IEnumerator iterator = _participants.GetEnumerator();
			while (iterator.MoveNext())
			{
				((com.db4o.@internal.TransactionParticipant)iterator.Current).Rollback(this);
			}
		}

		protected virtual void RollBackTransactionListeners()
		{
			CheckSynchronization();
			if (i_transactionListeners != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.MoveNext())
				{
					((com.db4o.TransactionListener)i.Current).PostRollback();
				}
				i_transactionListeners = null;
			}
		}

		internal virtual void SetAddress(int a_address)
		{
			i_address = a_address;
		}

		public abstract void SetPointer(int a_id, int a_address, int a_length);

		public virtual void SlotDelete(int a_id, int a_address, int a_length)
		{
		}

		public virtual void SlotFreeOnCommit(int a_id, int a_address, int a_length)
		{
		}

		internal virtual void SlotFreeOnRollback(int a_id, int a_address, int a_length)
		{
		}

		internal virtual void SlotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
		}

		internal virtual void SlotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length
			)
		{
		}

		public virtual void SlotFreePointerOnCommit(int a_id)
		{
		}

		internal virtual void SlotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
		}

		internal virtual bool SupportsVirtualFields()
		{
			return true;
		}

		public virtual com.db4o.@internal.Transaction SystemTransaction()
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

		public virtual void WritePointer(int a_id, int a_address, int a_length)
		{
			CheckSynchronization();
			i_pointerIo.UseSlot(a_id);
			i_pointerIo.WriteInt(a_address);
			i_pointerIo.WriteInt(a_length);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				i_pointerIo.SetID(com.db4o.@internal.Const4.IGNORE_ID);
			}
			i_pointerIo.Write();
		}

		public abstract void WriteUpdateDeleteMembers(int id, com.db4o.@internal.ClassMetadata
			 clazz, int typeInfo, int cascade);

		public com.db4o.@internal.ObjectContainerBase Stream()
		{
			return i_stream;
		}

		public virtual void Enlist(com.db4o.@internal.TransactionParticipant participant)
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

		public static com.db4o.@internal.Transaction ReadInterruptedTransaction(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader)
		{
			int transactionID1 = reader.ReadInt();
			int transactionID2 = reader.ReadInt();
			if ((transactionID1 > 0) && (transactionID1 == transactionID2))
			{
				com.db4o.@internal.Transaction transaction = file.NewTransaction(null);
				transaction.SetAddress(transactionID1);
				return transaction;
			}
			return null;
		}

		public virtual com.db4o.@internal.Transaction ParentTransaction()
		{
			return i_parentTransaction;
		}
	}
}
