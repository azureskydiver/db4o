namespace com.db4o
{
	internal class MsgObject : com.db4o.MsgD
	{
		private const int LENGTH_FOR_ALL = com.db4o.YapConst.YAPID_LENGTH + (com.db4o.YapConst
			.YAPINT_LENGTH * 3);

		private const int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;

		internal int _id;

		internal int _address;

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapWriter bytes, int[] prependInts
			)
		{
			int lengthNeeded = bytes.getLength() + LENGTH_FOR_FIRST;
			if (prependInts != null)
			{
				lengthNeeded += (prependInts.Length * com.db4o.YapConst.YAPINT_LENGTH);
			}
			int embeddedCount = bytes.embeddedCount();
			if (embeddedCount > 0)
			{
				lengthNeeded += (LENGTH_FOR_ALL * embeddedCount) + bytes.embeddedLength();
			}
			com.db4o.MsgD message = getWriterForLength(bytes.getTransaction(), lengthNeeded);
			if (prependInts != null)
			{
				for (int i = 0; i < prependInts.Length; i++)
				{
					message._payLoad.writeInt(prependInts[i]);
				}
			}
			message._payLoad.writeInt(embeddedCount);
			bytes.appendTo(message._payLoad, -1);
			return message;
		}

		internal override com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			return getWriter(bytes, null);
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 bytes)
		{
			return getWriter(bytes, new int[] { a_yapClass.getID() });
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapClass a_yapClass, int a_param
			, com.db4o.YapWriter bytes)
		{
			return getWriter(bytes, new int[] { a_yapClass.getID(), a_param });
		}

		public com.db4o.YapWriter unmarshall()
		{
			return unmarshall(0);
		}

		public com.db4o.YapWriter unmarshall(int addLengthBeforeFirst)
		{
			_payLoad.setTransaction(getTransaction());
			int embeddedCount = _payLoad.readInt();
			int length = _payLoad.readInt();
			if (length == 0)
			{
				return null;
			}
			_id = _payLoad.readInt();
			_address = _payLoad.readInt();
			if (embeddedCount == 0)
			{
				_payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
			}
			else
			{
				_payLoad._offset += length;
				com.db4o.YapWriter[] embedded = new com.db4o.YapWriter[embeddedCount + 1];
				embedded[0] = _payLoad;
				new com.db4o.YapWriter(_payLoad, embedded, 1);
				_payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
			}
			_payLoad.useSlot(_id, _address, length);
			return _payLoad;
		}
	}
}
