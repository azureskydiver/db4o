namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public abstract class PersistentBase : com.db4o.@internal.Persistent
	{
		protected int i_id;

		protected int i_state = 2;

		internal bool BeginProcessing()
		{
			if (BitIsTrue(com.db4o.@internal.Const4.PROCESSING))
			{
				return false;
			}
			BitTrue(com.db4o.@internal.Const4.PROCESSING);
			return true;
		}

		internal void BitFalse(int bitPos)
		{
			i_state &= ~(1 << bitPos);
		}

		internal bool BitIsFalse(int bitPos)
		{
			return (i_state | (1 << bitPos)) != i_state;
		}

		internal bool BitIsTrue(int bitPos)
		{
			return (i_state | (1 << bitPos)) == i_state;
		}

		internal void BitTrue(int bitPos)
		{
			i_state |= (1 << bitPos);
		}

		internal virtual void CacheDirty(com.db4o.foundation.Collection4 col)
		{
			if (!BitIsTrue(com.db4o.@internal.Const4.CACHED_DIRTY))
			{
				BitTrue(com.db4o.@internal.Const4.CACHED_DIRTY);
				col.Add(this);
			}
		}

		public virtual void EndProcessing()
		{
			BitFalse(com.db4o.@internal.Const4.PROCESSING);
		}

		public virtual int GetID()
		{
			return i_id;
		}

		public bool IsActive()
		{
			return BitIsTrue(com.db4o.@internal.Const4.ACTIVE);
		}

		public virtual bool IsDirty()
		{
			return BitIsTrue(com.db4o.@internal.Const4.ACTIVE) && (!BitIsTrue(com.db4o.@internal.Const4
				.CLEAN));
		}

		public bool IsNew()
		{
			return i_id == 0;
		}

		public virtual int LinkLength()
		{
			return com.db4o.@internal.Const4.ID_LENGTH;
		}

		internal void NotCachedDirty()
		{
			BitFalse(com.db4o.@internal.Const4.CACHED_DIRTY);
		}

		public virtual void Read(com.db4o.@internal.Transaction trans)
		{
			try
			{
				if (BeginProcessing())
				{
					com.db4o.@internal.Buffer reader = trans.Stream().ReadReaderByID(trans, GetID());
					if (reader != null)
					{
						ReadThis(trans, reader);
						SetStateOnRead(reader);
					}
					EndProcessing();
				}
			}
			catch (System.Exception t)
			{
			}
		}

		public virtual void SetID(int a_id)
		{
			i_id = a_id;
		}

		public void SetStateClean()
		{
			BitTrue(com.db4o.@internal.Const4.ACTIVE);
			BitTrue(com.db4o.@internal.Const4.CLEAN);
		}

		public void SetStateDeactivated()
		{
			BitFalse(com.db4o.@internal.Const4.ACTIVE);
		}

		public virtual void SetStateDirty()
		{
			BitTrue(com.db4o.@internal.Const4.ACTIVE);
			BitFalse(com.db4o.@internal.Const4.CLEAN);
		}

		internal virtual void SetStateOnRead(com.db4o.@internal.Buffer reader)
		{
			if (BitIsTrue(com.db4o.@internal.Const4.CACHED_DIRTY))
			{
				SetStateDirty();
			}
			else
			{
				SetStateClean();
			}
		}

		public void Write(com.db4o.@internal.Transaction trans)
		{
			if (!WriteObjectBegin())
			{
				return;
			}
			com.db4o.@internal.LocalObjectContainer stream = (com.db4o.@internal.LocalObjectContainer
				)trans.Stream();
			int address = 0;
			int length = OwnLength();
			com.db4o.@internal.Buffer writer = new com.db4o.@internal.Buffer(length);
			if (IsNew())
			{
				com.db4o.@internal.slots.Pointer4 ptr = stream.NewSlot(trans, length);
				SetID(ptr._id);
				address = ptr._address;
			}
			else
			{
				address = stream.GetSlot(length);
				trans.SlotFreeOnRollbackCommitSetPointer(i_id, address, length);
			}
			WriteThis(trans, writer);
			writer.WriteEncrypt(stream, address, 0);
			if (IsActive())
			{
				SetStateClean();
			}
			EndProcessing();
		}

		public virtual bool WriteObjectBegin()
		{
			if (IsDirty())
			{
				return BeginProcessing();
			}
			return false;
		}

		public virtual void WriteOwnID(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 writer)
		{
			Write(trans);
			writer.WriteInt(GetID());
		}

		public abstract byte GetIdentifier();

		public abstract int OwnLength();

		public abstract void ReadThis(com.db4o.@internal.Transaction arg1, com.db4o.@internal.Buffer
			 arg2);

		public abstract void WriteThis(com.db4o.@internal.Transaction arg1, com.db4o.@internal.Buffer
			 arg2);
	}
}
