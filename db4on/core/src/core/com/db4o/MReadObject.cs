namespace com.db4o
{
	internal sealed class MReadObject : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
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
				catch (System.Exception e)
				{
					bytes = null;
				}
			}
			if (bytes == null)
			{
				bytes = new com.db4o.YapWriter(this.GetTransaction(), 0, 0);
			}
			com.db4o.Msg.OBJECT_TO_CLIENT.GetWriter(bytes).Write(stream, sock);
			return true;
		}
	}
}
