namespace com.db4o.cs.messages
{
	public sealed class MReadMultipleObjects : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int size = ReadInt();
			com.db4o.cs.messages.MsgD[] ret = new com.db4o.cs.messages.MsgD[size];
			int length = (1 + size) * com.db4o.YapConst.INT_LENGTH;
			com.db4o.YapStream stream = GetStream();
			com.db4o.YapWriter bytes = null;
			lock (stream.i_lock)
			{
				for (int i = 0; i < size; i++)
				{
					int id = this._payLoad.ReadInt();
					try
					{
						bytes = stream.ReadWriterByID(GetTransaction(), id);
					}
					catch
					{
						bytes = null;
					}
					if (bytes != null)
					{
						ret[i] = com.db4o.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(bytes);
						length += ret[i]._payLoad.GetLength();
					}
				}
			}
			com.db4o.cs.messages.MsgD multibytes = com.db4o.cs.messages.Msg.READ_MULTIPLE_OBJECTS
				.GetWriterForLength(GetTransaction(), length);
			multibytes.WriteInt(size);
			for (int i = 0; i < size; i++)
			{
				if (ret[i] == null)
				{
					multibytes.WriteInt(0);
				}
				else
				{
					multibytes.WriteInt(ret[i]._payLoad.GetLength());
					multibytes._payLoad.Append(ret[i]._payLoad._buffer);
				}
			}
			multibytes.Write(stream, sock);
			return true;
		}
	}
}
