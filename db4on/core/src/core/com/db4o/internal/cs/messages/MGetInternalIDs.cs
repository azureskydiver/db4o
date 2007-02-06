namespace com.db4o.@internal.cs.messages
{
	public sealed class MGetInternalIDs : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.Buffer bytes = this.GetByteLoad();
			long[] ids;
			lock (StreamLock())
			{
				try
				{
					ids = Stream().GetYapClass(bytes.ReadInt()).GetIDs(Transaction());
				}
				catch
				{
					ids = new long[0];
				}
			}
			int size = ids.Length;
			com.db4o.@internal.cs.messages.MsgD message = com.db4o.@internal.cs.messages.Msg.
				ID_LIST.GetWriterForLength(Transaction(), com.db4o.@internal.Const4.ID_LENGTH * 
				(size + 1));
			com.db4o.@internal.Buffer writer = message.PayLoad();
			writer.WriteInt(size);
			for (int i = 0; i < size; i++)
			{
				writer.WriteInt((int)ids[i]);
			}
			serverThread.Write(message);
			return true;
		}
	}
}
