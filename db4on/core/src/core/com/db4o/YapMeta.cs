namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapMeta
	{
		internal int i_id = 0;

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

		internal abstract byte getIdentifier();

		public bool isActive()
		{
			return bitIsTrue(com.db4o.YapConst.ACTIVE);
		}

		public virtual bool isDirty()
		{
			return bitIsTrue(com.db4o.YapConst.ACTIVE) && (!bitIsTrue(com.db4o.YapConst.CLEAN
				));
		}

		public virtual int linkLength()
		{
			return com.db4o.YapConst.YAPID_LENGTH;
		}

		internal void notCachedDirty()
		{
			bitFalse(com.db4o.YapConst.CACHED_DIRTY);
		}

		internal abstract int ownLength();

		internal virtual void read(com.db4o.Transaction a_trans)
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

		internal abstract void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader 
			a_reader);

		internal virtual void setID(com.db4o.YapStream a_stream, int a_id)
		{
			i_id = a_id;
		}

		internal void setStateClean()
		{
			bitTrue(com.db4o.YapConst.ACTIVE);
			bitTrue(com.db4o.YapConst.CLEAN);
		}

		internal void setStateDeactivated()
		{
			bitFalse(com.db4o.YapConst.ACTIVE);
		}

		internal virtual void setStateDirty()
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

		internal virtual com.db4o.YapWriter write(com.db4o.YapStream a_stream, com.db4o.Transaction
			 a_trans)
		{
			if (writeObjectBegin())
			{
				com.db4o.YapWriter writer = (getID() == 0) ? a_stream.newObject(a_trans, this) : 
					a_stream.updateObject(a_trans, this);
				writeThis(writer);
				((com.db4o.YapFile)a_stream).writeObject(this, writer);
				if (isActive())
				{
					setStateClean();
				}
				endProcessing();
				return writer;
			}
			return null;
		}

		internal virtual bool writeObjectBegin()
		{
			if (isDirty())
			{
				return beginProcessing();
			}
			return false;
		}

		internal virtual void writeOwnID(com.db4o.YapWriter a_writer)
		{
			write(a_writer.getStream(), a_writer.getTransaction());
			a_writer.writeInt(getID());
		}

		internal abstract void writeThis(com.db4o.YapWriter a_writer);

		internal static void writeIDOf(com.db4o.YapMeta a_object, com.db4o.YapWriter a_writer
			)
		{
			if (a_object != null)
			{
				a_object.writeOwnID(a_writer);
			}
			else
			{
				a_writer.writeInt(0);
			}
		}
	}
}
