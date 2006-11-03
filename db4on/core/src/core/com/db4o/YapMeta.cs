namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapMeta
	{
		protected int i_id;

		protected int i_state = 2;

		internal bool BeginProcessing()
		{
			if (BitIsTrue(com.db4o.YapConst.PROCESSING))
			{
				return false;
			}
			BitTrue(com.db4o.YapConst.PROCESSING);
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
			if (!BitIsTrue(com.db4o.YapConst.CACHED_DIRTY))
			{
				BitTrue(com.db4o.YapConst.CACHED_DIRTY);
				col.Add(this);
			}
		}

		public virtual void EndProcessing()
		{
			BitFalse(com.db4o.YapConst.PROCESSING);
		}

		public virtual int GetID()
		{
			return i_id;
		}

		public abstract byte GetIdentifier();

		public bool IsActive()
		{
			return BitIsTrue(com.db4o.YapConst.ACTIVE);
		}

		public virtual bool IsDirty()
		{
			return BitIsTrue(com.db4o.YapConst.ACTIVE) && (!BitIsTrue(com.db4o.YapConst.CLEAN
				));
		}

		public virtual bool IsNew()
		{
			return i_id == 0;
		}

		public virtual int LinkLength()
		{
			return com.db4o.YapConst.ID_LENGTH;
		}

		internal void NotCachedDirty()
		{
			BitFalse(com.db4o.YapConst.CACHED_DIRTY);
		}

		public abstract int OwnLength();

		public virtual void Read(com.db4o.Transaction trans)
		{
			try
			{
				if (BeginProcessing())
				{
					com.db4o.YapReader reader = trans.Stream().ReadReaderByID(trans, GetID());
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

		public abstract void ReadThis(com.db4o.Transaction trans, com.db4o.YapReader reader
			);

		public virtual void SetID(int a_id)
		{
			i_id = a_id;
		}

		public void SetStateClean()
		{
			BitTrue(com.db4o.YapConst.ACTIVE);
			BitTrue(com.db4o.YapConst.CLEAN);
		}

		public void SetStateDeactivated()
		{
			BitFalse(com.db4o.YapConst.ACTIVE);
		}

		public virtual void SetStateDirty()
		{
			BitTrue(com.db4o.YapConst.ACTIVE);
			BitFalse(com.db4o.YapConst.CLEAN);
		}

		internal virtual void SetStateOnRead(com.db4o.YapReader reader)
		{
			if (BitIsTrue(com.db4o.YapConst.CACHED_DIRTY))
			{
				SetStateDirty();
			}
			else
			{
				SetStateClean();
			}
		}

		public void Write(com.db4o.Transaction trans)
		{
			if (!WriteObjectBegin())
			{
				return;
			}
			com.db4o.YapFile stream = (com.db4o.YapFile)trans.Stream();
			int address = 0;
			int length = OwnLength();
			com.db4o.YapReader writer = new com.db4o.YapReader(length);
			if (IsNew())
			{
				com.db4o.inside.slots.Pointer4 ptr = stream.NewSlot(trans, length);
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

		public virtual void WriteOwnID(com.db4o.Transaction trans, com.db4o.YapReader writer
			)
		{
			Write(trans);
			writer.WriteInt(GetID());
		}

		public abstract void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader writer
			);
	}
}
