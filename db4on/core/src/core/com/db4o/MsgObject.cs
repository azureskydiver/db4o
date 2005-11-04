namespace com.db4o
{
	internal class MsgObject : com.db4o.MsgD
	{
		private const int LENGTH_FOR_ALL = com.db4o.YapConst.YAPID_LENGTH + (com.db4o.YapConst
			.YAPINT_LENGTH * 3);

		private const int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;

		internal int i_id;

		internal int i_address;

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
					message.payLoad.writeInt(prependInts[i]);
				}
			}
			message.payLoad.writeInt(embeddedCount);
			bytes.appendTo(message.payLoad, -1);
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
			payLoad.setTransaction(getTransaction());
			int embeddedCount = payLoad.readInt();
			int length = payLoad.readInt();
			if (length == 0)
			{
				return null;
			}
			i_id = payLoad.readInt();
			i_address = payLoad.readInt();
			if (embeddedCount == 0)
			{
				payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
			}
			else
			{
				payLoad._offset += length;
				com.db4o.YapWriter[] embedded = new com.db4o.YapWriter[embeddedCount + 1];
				embedded[0] = payLoad;
				new com.db4o.YapWriter(payLoad, embedded, 1);
				payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
			}
			payLoad.useSlot(i_id, i_address, length);
			return payLoad;
		}
	}
}
