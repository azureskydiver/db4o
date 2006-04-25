namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapMeta
	{
		internal int i_id;

		protected int i_state = 2;

		internal bool beginProcessing()
		{
			if (bitIsTrue(com.db4o.YapConst.PROCESSING))
			{
				return false;
			}
			bitTrue(com.db4o.YapConst.PROCESSING);
			return true;
		}

		internal void bitFalse(int bitPos)
		{
			i_state &= ~(1 << bitPos);
		}

		internal bool bitIsFalse(int bitPos)
		{
			return (i_state | (1 << bitPos)) != i_state;
		}

		internal bool bitIsTrue(int bitPos)
		{
			return (i_state | (1 << bitPos)) == i_state;
		}

		internal void bitTrue(int bitPos)
		{
			i_state |= (1 << bitPos);
		}

		internal virtual void cacheDirty(com.db4o.foundation.Collection4 col)
		{
			if (!bitIsTrue(com.db4o.YapConst.CACHED_DIRTY))
			{
				bitTrue(com.db4o.YapConst.CACHED_DIRTY);
				col.add(this);
			}
		}

		internal virtual void endProcessing()
		{
			bitFalse(com.db4o.YapConst.PROCESSING);
		}

		public virtual int getID()
		{
			return i_id;
		}

		public abstract byte getIdentifier();

		public bool isActive()
		{
			return bitIsTrue(com.db4o.YapConst.ACTIVE);
		}

		public virtual bool isDirty()
		{
			return bitIsTrue(com.db4o.YapConst.ACTIVE) && (!bitIsTrue(com.db4o.YapConst.CLEAN
				));
		}

		public virtual bool isNew()
		{
			return i_id == 0;
		}

		public virtual int linkLength()
		{
			return com.db4o.YapConst.YAPID_LENGTH;
		}

		internal void notCachedDirty()
		{
			bitFalse(com.db4o.YapConst.CACHED_DIRTY);
		}

		public abstract int ownLength();

		public virtual void read(com.db4o.Transaction a_trans)
		{
			try
			{
				if (beginProcessing())
				{
					com.db4o.YapReader reader = a_trans.i_stream.readReaderByID(a_trans, getID());
					if (reader != null)
					{
						readThis(a_trans, reader);
						setStateOnRead(reader);
					}
					endProcessing();
				}
			}
			catch (com.db4o.LongJumpOutException ljoe)
			{
				throw ljoe;
			}
			catch (System.Exception t)
			{
			}
		}

		public abstract void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			);

		public virtual void setID(int a_id)
		{
			i_id = a_id;
		}

		public void setStateClean()
		{
			bitTrue(com.db4o.YapConst.ACTIVE);
			bitTrue(com.db4o.YapConst.CLEAN);
		}

		public void setStateDeactivated()
		{
			bitFalse(com.db4o.YapConst.ACTIVE);
		}

		public virtual void setStateDirty()
		{
			bitTrue(com.db4o.YapConst.ACTIVE);
			bitFalse(com.db4o.YapConst.CLEAN);
		}

		internal virtual void setStateOnRead(com.db4o.YapReader reader)
		{
			if (bitIsTrue(com.db4o.YapConst.CACHED_DIRTY))
			{
				setStateDirty();
			}
			else
			{
				setStateClean();
			}
		}

		public void write(com.db4o.Transaction a_trans)
		{
			if (!writeObjectBegin())
			{
				return;
			}
			com.db4o.YapFile stream = (com.db4o.YapFile)a_trans.i_stream;
			int address = 0;
			int length = ownLength();
			com.db4o.YapReader writer = new com.db4o.YapReader(length);
			if (isNew())
			{
				com.db4o.inside.slots.Pointer4 ptr = stream.newSlot(a_trans, length);
				setID(ptr._id);
				address = ptr._address;
			}
			else
			{
				address = stream.getSlot(length);
				a_trans.slotFreeOnRollbackCommitSetPointer(i_id, address, length);
			}
			writeThis(a_trans, writer);
			((com.db4o.YapFile)stream).writeObject(this, writer, address);
			if (isActive())
			{
				setStateClean();
			}
			endProcessing();
		}

		internal virtual bool writeObjectBegin()
		{
			if (isDirty())
			{
				return beginProcessing();
			}
			return false;
		}

		internal virtual void writeOwnID(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			write(trans);
			a_writer.writeInt(getID());
		}

		public abstract void writeThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			);
	}
}
