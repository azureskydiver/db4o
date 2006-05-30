namespace com.db4o
{
	internal sealed class MReleaseSemaphore : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			string name = ReadString();
			((com.db4o.YapFile)GetStream()).ReleaseSemaphore(GetTransaction(), name);
			return true;
		}
	}
}
