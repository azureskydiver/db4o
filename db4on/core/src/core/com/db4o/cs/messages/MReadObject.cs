namespace com.db4o.cs.messages
{
	public sealed class MReadObject : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = null;
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					bytes = stream.ReadWriterByID(this.GetTransaction(), this._payLoad.ReadInt());
				}
				catch
				{
				}
			}
			if (bytes == null)
			{
				bytes = new com.db4o.YapWriter(this.GetTransaction(), 0, 0);
			}
			com.db4o.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(bytes).Write(stream, sock);
			return true;
		}
	}
}
