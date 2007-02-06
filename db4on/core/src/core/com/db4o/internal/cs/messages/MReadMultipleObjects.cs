namespace com.db4o.@internal.cs.messages
{
	public sealed class MReadMultipleObjects : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int size = ReadInt();
			com.db4o.@internal.cs.messages.MsgD[] ret = new com.db4o.@internal.cs.messages.MsgD
				[size];
			int length = (1 + size) * com.db4o.@internal.Const4.INT_LENGTH;
			lock (StreamLock())
			{
				for (int i = 0; i < size; i++)
				{
					int id = this._payLoad.ReadInt();
					try
					{
						com.db4o.@internal.StatefulBuffer bytes = Stream().ReadWriterByID(Transaction(), 
							id);
						if (bytes != null)
						{
							ret[i] = com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(bytes);
							length += ret[i]._payLoad.GetLength();
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			com.db4o.@internal.cs.messages.MsgD multibytes = com.db4o.@internal.cs.messages.Msg
				.READ_MULTIPLE_OBJECTS.GetWriterForLength(Transaction(), length);
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
			serverThread.Write(multibytes);
			return true;
		}
	}
}
