namespace com.db4o
{
	internal sealed class MSetSemaphore : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int timeout = readInt();
			string name = readString();
			com.db4o.YapFile stream = (com.db4o.YapFile)getStream();
			bool res = stream.setSemaphore(getTransaction(), name, timeout);
			if (res)
			{
				com.db4o.Msg.SUCCESS.write(stream, sock);
			}
			else
			{
				com.db4o.Msg.FAILED.write(stream, sock);
			}
			return true;
		}
	}
}
