namespace com.db4o.@internal.cs.messages
{
	/// <summary>Messages with Data for Client/Server Communication</summary>
	public class MsgD : com.db4o.@internal.cs.messages.Msg
	{
		internal com.db4o.@internal.StatefulBuffer _payLoad;

		internal MsgD() : base()
		{
		}

		internal MsgD(string aName) : base(aName)
		{
		}

		internal override void FakePayLoad(com.db4o.@internal.Transaction a_trans)
		{
		}

		public override com.db4o.@internal.Buffer GetByteLoad()
		{
			return _payLoad;
		}

		public sealed override com.db4o.@internal.StatefulBuffer PayLoad()
		{
			return _payLoad;
		}

		public virtual void PayLoad(com.db4o.@internal.StatefulBuffer writer)
		{
			_payLoad = writer;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForLength(com.db4o.@internal.Transaction
			 a_trans, int length)
		{
			com.db4o.@internal.cs.messages.MsgD message = (com.db4o.@internal.cs.messages.MsgD
				)Clone(a_trans);
			message._payLoad = new com.db4o.@internal.StatefulBuffer(a_trans, length + com.db4o.@internal.Const4
				.MESSAGE_LENGTH);
			message.WriteInt(_msgID);
			message.WriteInt(length);
			if (a_trans.ParentTransaction() == null)
			{
				message._payLoad.Append(com.db4o.@internal.Const4.SYSTEM_TRANS);
			}
			else
			{
				message._payLoad.Append(com.db4o.@internal.Const4.USER_TRANS);
			}
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriter(com.db4o.@internal.Transaction
			 a_trans)
		{
			return GetWriterForLength(a_trans, 0);
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForInts(com.db4o.@internal.Transaction
			 a_trans, int[] ints)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.INT_LENGTH * ints.Length);
			for (int i = 0; i < ints.Length; i++)
			{
				message.WriteInt(ints[i]);
			}
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForIntArray(com.db4o.@internal.Transaction
			 a_trans, int[] ints, int length)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.INT_LENGTH * (length + 1));
			message.WriteInt(length);
			for (int i = 0; i < length; i++)
			{
				message.WriteInt(ints[i]);
			}
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForInt(com.db4o.@internal.Transaction
			 a_trans, int id)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.INT_LENGTH);
			message.WriteInt(id);
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForIntString(com.db4o.@internal.Transaction
			 a_trans, int anInt, string str)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.stringIO.Length(str) + com.db4o.@internal.Const4.INT_LENGTH * 2);
			message.WriteInt(anInt);
			message.WriteString(str);
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForLong(com.db4o.@internal.Transaction
			 a_trans, long a_long)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.LONG_LENGTH);
			message.WriteLong(a_long);
			return message;
		}

		public com.db4o.@internal.cs.messages.MsgD GetWriterForString(com.db4o.@internal.Transaction
			 a_trans, string str)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(a_trans, com.db4o.@internal.Const4
				.stringIO.Length(str) + com.db4o.@internal.Const4.INT_LENGTH);
			message.WriteString(str);
			return message;
		}

		public virtual com.db4o.@internal.cs.messages.MsgD GetWriter(com.db4o.@internal.StatefulBuffer
			 bytes)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(bytes.GetTransaction
				(), bytes.GetLength());
			message._payLoad.Append(bytes._buffer);
			return message;
		}

		public virtual byte[] ReadBytes()
		{
			return _payLoad.ReadBytes(ReadInt());
		}

		public int ReadInt()
		{
			return _payLoad.ReadInt();
		}

		public long ReadLong()
		{
			return _payLoad.ReadLong();
		}

		public bool ReadBoolean()
		{
			return _payLoad.ReadByte() != 0;
		}

		internal sealed override com.db4o.@internal.cs.messages.Msg ReadPayLoad(com.db4o.@internal.Transaction
			 a_trans, com.db4o.foundation.network.Socket4 sock, com.db4o.@internal.Buffer reader
			)
		{
			int length = reader.ReadInt();
			a_trans = CheckParentTransaction(a_trans, reader);
			com.db4o.@internal.cs.messages.MsgD command = (com.db4o.@internal.cs.messages.MsgD
				)Clone(a_trans);
			command._payLoad = new com.db4o.@internal.StatefulBuffer(a_trans, length);
			command._payLoad.Read(sock);
			return command;
		}

		public string ReadString()
		{
			int length = ReadInt();
			return com.db4o.@internal.Const4.stringIO.Read(_payLoad, length);
		}

		public void WriteBytes(byte[] aBytes)
		{
			WriteInt(aBytes.Length);
			_payLoad.Append(aBytes);
		}

		public void WriteInt(int aInt)
		{
			_payLoad.WriteInt(aInt);
		}

		public void WriteLong(long l)
		{
			_payLoad.WriteLong(l);
		}

		public void WriteString(string aStr)
		{
			_payLoad.WriteInt(aStr.Length);
			com.db4o.@internal.Const4.stringIO.Write(_payLoad, aStr);
		}
	}
}
