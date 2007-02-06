namespace com.db4o.@internal.cs.messages
{
	public sealed class MReadBytes : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override com.db4o.@internal.Buffer GetByteLoad()
		{
			int address = _payLoad.ReadInt();
			int length = _payLoad.GetLength() - (com.db4o.@internal.Const4.INT_LENGTH);
			_payLoad.RemoveFirstBytes(com.db4o.@internal.Const4.INT_LENGTH);
			_payLoad.UseSlot(address, length);
			return this._payLoad;
		}

		public sealed override com.db4o.@internal.cs.messages.MsgD GetWriter(com.db4o.@internal.StatefulBuffer
			 bytes)
		{
			com.db4o.@internal.cs.messages.MsgD message = GetWriterForLength(bytes.GetTransaction
				(), bytes.GetLength() + com.db4o.@internal.Const4.INT_LENGTH);
			message._payLoad.WriteInt(bytes.GetAddress());
			message._payLoad.Append(bytes._buffer);
			return message;
		}

		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int address = ReadInt();
			int length = ReadInt();
			lock (StreamLock())
			{
				com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(this
					.Transaction(), address, length);
				try
				{
					Stream().ReadBytes(bytes._buffer, address, length);
					serverThread.Write(GetWriter(bytes));
				}
				catch
				{
					serverThread.Write(com.db4o.@internal.cs.messages.Msg.NULL);
				}
			}
			return true;
		}
	}
}
