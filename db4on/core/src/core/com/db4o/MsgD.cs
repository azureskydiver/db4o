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

		internal override void FakePayLoad(com.db4o.Transaction a_trans)
		{
		}

		internal override com.db4o.YapWriter GetByteLoad()
		{
			return _payLoad;
		}

		internal sealed override com.db4o.YapWriter GetPayLoad()
		{
			return _payLoad;
		}

		internal com.db4o.MsgD GetWriterForLength(com.db4o.Transaction a_trans, int length
			)
		{
			com.db4o.MsgD message = (com.db4o.MsgD)Clone(a_trans);
			message._payLoad = new com.db4o.YapWriter(a_trans, length + com.db4o.YapConst.MESSAGE_LENGTH
				);
			message.WriteInt(_msgID);
			message.WriteInt(length);
			if (a_trans.i_parentTransaction == null)
			{
				message._payLoad.Append(com.db4o.YapConst.SYSTEM_TRANS);
			}
			else
			{
				message._payLoad.Append(com.db4o.YapConst.USER_TRANS);
			}
			return message;
		}

		internal com.db4o.MsgD GetWriter(com.db4o.Transaction a_trans)
		{
			return GetWriterForLength(a_trans, 0);
		}

		internal com.db4o.MsgD GetWriterForInts(com.db4o.Transaction a_trans, int[] ints)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.INT_LENGTH 
				* ints.Length);
			for (int i = 0; i < ints.Length; i++)
			{
				message.WriteInt(ints[i]);
			}
			return message;
		}

		internal com.db4o.MsgD GetWriterForIntArray(com.db4o.Transaction a_trans, int[] ints
			, int length)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.INT_LENGTH 
				* (length + 1));
			message.WriteInt(length);
			for (int i = 0; i < length; i++)
			{
				message.WriteInt(ints[i]);
			}
			return message;
		}

		internal com.db4o.MsgD GetWriterForInt(com.db4o.Transaction a_trans, int id)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.INT_LENGTH);
			message.WriteInt(id);
			return message;
		}

		internal com.db4o.MsgD GetWriterForIntString(com.db4o.Transaction a_trans, int anInt
			, string str)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.stringIO.Length
				(str) + com.db4o.YapConst.INT_LENGTH * 2);
			message.WriteInt(anInt);
			message.WriteString(str);
			return message;
		}

		internal com.db4o.MsgD GetWriterForLong(com.db4o.Transaction a_trans, long a_long
			)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.LONG_LENGTH
				);
			message.WriteLong(a_long);
			return message;
		}

		internal com.db4o.MsgD GetWriterForString(com.db4o.Transaction a_trans, string str
			)
		{
			com.db4o.MsgD message = GetWriterForLength(a_trans, com.db4o.YapConst.stringIO.Length
				(str) + com.db4o.YapConst.INT_LENGTH);
			message.WriteString(str);
			return message;
		}

		internal virtual com.db4o.MsgD GetWriter(com.db4o.YapWriter bytes)
		{
			com.db4o.MsgD message = GetWriterForLength(bytes.GetTransaction(), bytes.GetLength
				());
			message._payLoad.Append(bytes._buffer);
			return message;
		}

		internal virtual byte[] ReadBytes()
		{
			return _payLoad.ReadBytes(ReadInt());
		}

		internal int ReadInt()
		{
			return _payLoad.ReadInt();
		}

		internal long ReadLong()
		{
			return com.db4o.YLong.ReadLong(_payLoad);
		}

		internal sealed override com.db4o.Msg ReadPayLoad(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock, com.db4o.YapWriter reader)
		{
			int length = reader.ReadInt();
			if ((reader.ReadByte() == com.db4o.YapConst.SYSTEM_TRANS) && (a_trans.i_parentTransaction
				 != null))
			{
				a_trans = a_trans.i_parentTransaction;
			}
			com.db4o.MsgD command = (com.db4o.MsgD)Clone(a_trans);
			command._payLoad = new com.db4o.YapWriter(a_trans, length);
			command._payLoad.Read(sock);
			return command;
		}

		internal string ReadString()
		{
			int length = ReadInt();
			return com.db4o.YapConst.stringIO.Read(_payLoad, length);
		}

		internal void WriteBytes(byte[] aBytes)
		{
			WriteInt(aBytes.Length);
			_payLoad.Append(aBytes);
		}

		internal void WriteInt(int aInt)
		{
			_payLoad.WriteInt(aInt);
		}

		internal void WriteLong(long aLong)
		{
			com.db4o.YLong.WriteLong(aLong, _payLoad);
		}

		internal void WriteString(string aStr)
		{
			_payLoad.WriteInt(aStr.Length);
			com.db4o.YapConst.stringIO.Write(_payLoad, aStr);
		}
	}
}
