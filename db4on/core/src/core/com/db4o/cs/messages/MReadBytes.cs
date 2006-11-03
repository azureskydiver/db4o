namespace com.db4o.cs.messages
{
	public sealed class MReadBytes : com.db4o.cs.messages.MsgD
	{
		public sealed override com.db4o.YapReader GetByteLoad()
		{
			int address = this._payLoad.ReadInt();
			int length = this._payLoad.GetLength() - (com.db4o.YapConst.INT_LENGTH);
			this._payLoad.RemoveFirstBytes(com.db4o.YapConst.INT_LENGTH);
			this._payLoad.UseSlot(address, length);
			return this._payLoad;
		}

		public sealed override com.db4o.cs.messages.MsgD GetWriter(com.db4o.YapWriter bytes
			)
		{
			com.db4o.cs.messages.MsgD message = this.GetWriterForLength(bytes.GetTransaction(
				), bytes.GetLength() + com.db4o.YapConst.INT_LENGTH);
			message._payLoad.WriteInt(bytes.GetAddress());
			message._payLoad.Append(bytes._buffer);
			return message;
		}

		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			int address = this.ReadInt();
			int length = this.ReadInt();
			lock (stream.i_lock)
			{
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this.GetTransaction(), address, 
					length);
				try
				{
					stream.ReadBytes(bytes._buffer, address, length);
					GetWriter(bytes).Write(stream, sock);
				}
				catch
				{
					com.db4o.cs.messages.Msg.NULL.Write(stream, sock);
				}
			}
			return true;
		}
	}
}
