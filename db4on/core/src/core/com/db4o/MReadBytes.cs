namespace com.db4o
{
	internal sealed class MReadBytes : com.db4o.MsgD
	{
		internal sealed override com.db4o.YapWriter getByteLoad()
		{
			int address = this.payLoad.readInt();
			int length = this.payLoad.getLength() - (com.db4o.YapConst.YAPINT_LENGTH);
			this.payLoad.removeFirstBytes(com.db4o.YapConst.YAPINT_LENGTH);
			this.payLoad.useSlot(address, length);
			return this.payLoad;
		}

		internal sealed override com.db4o.MsgD getWriter(com.db4o.YapWriter bytes)
		{
			com.db4o.MsgD message = this.getWriterForLength(bytes.getTransaction(), bytes.getLength
				() + com.db4o.YapConst.YAPINT_LENGTH);
			message.payLoad.writeInt(bytes.getAddress());
			message.payLoad.append(bytes._buffer);
			return message;
		}

		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			int address = this.readInt();
			int length = this.readInt();
			lock (stream.i_lock)
			{
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this.getTransaction(), address, 
					length);
				try
				{
					stream.readBytes(bytes._buffer, address, length);
					getWriter(bytes).write(stream, sock);
				}
				catch (System.Exception e)
				{
					com.db4o.Msg.NULL.write(stream, sock);
				}
			}
			return true;
		}
	}
}
