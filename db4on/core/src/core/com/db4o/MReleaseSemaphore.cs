namespace com.db4o
{
	internal sealed class MReleaseSemaphore : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			string name = readString();
			((com.db4o.YapFile)getStream()).releaseSemaphore(getTransaction(), name);
			return true;
		}
	}
}
