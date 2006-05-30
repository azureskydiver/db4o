namespace com.db4o
{
	internal sealed class MSetSemaphore : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int timeout = ReadInt();
			string name = ReadString();
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			bool res = stream.SetSemaphore(GetTransaction(), name, timeout);
			if (res)
			{
				com.db4o.Msg.SUCCESS.Write(stream, sock);
			}
			else
			{
				com.db4o.Msg.FAILED.Write(stream, sock);
			}
			return true;
		}
	}
}
