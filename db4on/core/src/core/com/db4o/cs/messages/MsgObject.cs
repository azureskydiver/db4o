namespace com.db4o.cs.messages
{
	public class MsgObject : com.db4o.cs.messages.MsgD
	{
		private const int LENGTH_FOR_ALL = com.db4o.YapConst.ID_LENGTH + (com.db4o.YapConst
			.INT_LENGTH * 3);

		private const int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;

		internal int _id;

		internal int _address;

		internal virtual com.db4o.cs.messages.MsgD GetWriter(com.db4o.YapWriter bytes, int[]
			 prependInts)
		{
			int lengthNeeded = bytes.GetLength() + LENGTH_FOR_FIRST;
			if (prependInts != null)
			{
				lengthNeeded += (prependInts.Length * com.db4o.YapConst.INT_LENGTH);
			}
			int embeddedCount = bytes.EmbeddedCount();
			if (embeddedCount > 0)
			{
				lengthNeeded += (LENGTH_FOR_ALL * embeddedCount) + bytes.EmbeddedLength();
			}
			com.db4o.cs.messages.MsgD message = GetWriterForLength(bytes.GetTransaction(), lengthNeeded
				);
			if (prependInts != null)
			{
				for (int i = 0; i < prependInts.Length; i++)
				{
					message._payLoad.WriteInt(prependInts[i]);
				}
			}
			message._payLoad.WriteInt(embeddedCount);
			bytes.AppendTo(message._payLoad, -1);
			return message;
		}

		public override com.db4o.cs.messages.MsgD GetWriter(com.db4o.YapWriter bytes)
		{
			return GetWriter(bytes, null);
		}

		public virtual com.db4o.cs.messages.MsgD GetWriter(com.db4o.YapClass a_yapClass, 
			com.db4o.YapWriter bytes)
		{
			if (a_yapClass == null)
			{
				return GetWriter(bytes, new int[] { 0 });
			}
			return GetWriter(bytes, new int[] { a_yapClass.GetID() });
		}

		public virtual com.db4o.cs.messages.MsgD GetWriter(com.db4o.YapClass a_yapClass, 
			int a_param, com.db4o.YapWriter bytes)
		{
			return GetWriter(bytes, new int[] { a_yapClass.GetID(), a_param });
		}

		public com.db4o.YapWriter Unmarshall()
		{
			return Unmarshall(0);
		}

		public com.db4o.YapWriter Unmarshall(int addLengthBeforeFirst)
		{
			_payLoad.SetTransaction(GetTransaction());
			int embeddedCount = _payLoad.ReadInt();
			int length = _payLoad.ReadInt();
			if (length == 0)
			{
				return null;
			}
			_id = _payLoad.ReadInt();
			_address = _payLoad.ReadInt();
			if (embeddedCount == 0)
			{
				_payLoad.RemoveFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
			}
			else
			{
				_payLoad._offset += length;
				com.db4o.YapWriter[] embedded = new com.db4o.YapWriter[embeddedCount + 1];
				embedded[0] = _payLoad;
				new com.db4o.YapWriter(_payLoad, embedded, 1);
				_payLoad.Trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
			}
			_payLoad.UseSlot(_id, _address, length);
			return _payLoad;
		}

		public virtual void PayLoad(com.db4o.YapWriter writer)
		{
			_payLoad = writer;
		}

		public virtual int GetId()
		{
			return _id;
		}
	}
}
