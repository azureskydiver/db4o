namespace com.db4o
{
	/// <summary>Messages with Data for Client/Server Communication</summary>
	internal class MsgD : com.db4o.Msg
	{
		internal com.db4o.YapWriter _payLoad;

		internal MsgD() : base()
		{
		}

		internal MsgD(string aName) : base(aName)
		{
		}

		internal override void fakePayLoad(com.db4o.Transaction a_trans)
		{
		}

		internal override com.db4o.YapWriter getByteLoad()
		{
			return _payLoad;
		}

		internal sealed override com.db4o.YapWriter getPayLoad()
		{
			return _payLoad;
		}

		internal com.db4o.MsgD getWriterForLength(com.db4o.Transaction a_trans, int length
			)
		{
			com.db4o.MsgD message = (com.db4o.MsgD)clone(a_trans);
			message._payLoad = new com.db4o.YapWriter(a_trans, length + com.db4o.YapConst.MESSAGE_LENGTH
				);
			message.writeInt(_msgID);
			message.writeInt(length);
			if (a_trans.i_parentTransaction == null)
			{
				message._payLoad.append(com.db4o.YapConst.SYSTEM_TRANS);
			}
			else
			{
				message._payLoad.append(com.db4o.YapConst.USER_TRANS);
			}
			return message;
		}

		internal com.db4o.MsgD getWriter(com.db4o.Transaction a_trans)
		{
			return getWriterForLength(a_trans, 0);
		}

		internal com.db4o.MsgD getWriterForInts(com.db4o.Transaction a_trans, int[] ints)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * ints.Length);
			for (int i = 0; i < ints.Length; i++)
			{
				message.writeInt(ints[i]);
			}
			return message;
		}

		internal com.db4o.MsgD getWriterForIntArray(com.db4o.Transaction a_trans, int[] ints
			, int length)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				 * (length + 1));
			message.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				message.writeInt(ints[i]);
			}
			return message;
		}

		internal com.db4o.MsgD getWriterForInt(com.db4o.Transaction a_trans, int id)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPINT_LENGTH
				);
			message.writeInt(id);
			return message;
		}

		internal com.db4o.MsgD getWriterForIntString(com.db4o.Transaction a_trans, int anInt
			, string str)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.stringIO.length
				(str) + com.db4o.YapConst.YAPINT_LENGTH * 2);
			message.writeInt(anInt);
			message.writeString(str);
			return message;
		}

		internal com.db4o.MsgD getWriterForLong(com.db4o.Transaction a_trans, long a_long
			)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.YAPLONG_LENGTH
				);
			message.writeLong(a_long);
			return message;
		}

		internal com.db4o.MsgD getWriterForString(com.db4o.Transaction a_trans, string str
			)
		{
			com.db4o.MsgD message = getWriterForLength(a_trans, com.db4o.YapConst.stringIO.length
				(str) + com.db4o.YapConst.YAPINT_LENGTH);
			message.writeString(str);
			return message;
		}

		internal virtual com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			com.db4o.MsgD message = getWriterForLength(bytes.getTransaction(), bytes.getLength
				());
			message._payLoad.append(bytes._buffer);
			return message;
		}

		internal virtual byte[] readBytes()
		{
			return _payLoad.readBytes(readInt());
		}

		internal int readInt()
		{
			return _payLoad.readInt();
		}

		internal long readLong()
		{
			return com.db4o.YLong.readLong(_payLoad);
		}

		internal sealed override com.db4o.Msg readPayLoad(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock, com.db4o.YapWriter reader)
		{
			int length = reader.readInt();
			if ((reader.readByte() == com.db4o.YapConst.SYSTEM_TRANS) && (a_trans.i_parentTransaction
				 != null))
			{
				a_trans = a_trans.i_parentTransaction;
			}
			com.db4o.MsgD command = (com.db4o.MsgD)clone(a_trans);
			command._payLoad = new com.db4o.YapWriter(a_trans, length);
			command._payLoad.read(sock);
			return command;
		}

		internal string readString()
		{
			int length = readInt();
			return com.db4o.YapConst.stringIO.read(_payLoad, length);
		}

		internal void writeBytes(byte[] aBytes)
		{
			writeInt(aBytes.Length);
			_payLoad.append(aBytes);
		}

		internal void writeInt(int aInt)
		{
			_payLoad.writeInt(aInt);
		}

		internal void writeLong(long aLong)
		{
			com.db4o.YLong.writeLong(aLong, _payLoad);
		}

		internal void writeString(string aStr)
		{
			_payLoad.writeInt(j4o.lang.JavaSystem.getLengthOf(aStr));
			com.db4o.YapConst.stringIO.write(_payLoad, aStr);
		}
	}
}
